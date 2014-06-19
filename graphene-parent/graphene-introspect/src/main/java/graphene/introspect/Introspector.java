package graphene.introspect;

import graphene.util.ExceptionUtil;
import graphene.util.db.DBConnectionPoolService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.mysema.query.sql.HSQLDBTemplates;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

/**
 * 
 * Based initially on QueryDSL's MetaDataIntrospector
 * 
 * Given a database connection, we wish to get a list of all tables and columns.
 * Then we perform simple 0 order analytics on the tables:
 * 
 * 
 * For each table:
 * 
 * Table Row count
 * 
 * For each column:
 * 
 * Number of values (non unique), Number of unique values
 * 
 * Compare number of unique to non unique (is that column behaving like a
 * primary key)
 * 
 * For String type datatype columns, look at the defined length of the column
 * and also the min,avg,max of the actual lengths.
 * 
 * For numeric, date and string columns, look at the min, max and avg and number
 * of non null and number of unique values
 * 
 * 
 * 
 * Generate guesses of table column meaning based on name of column
 * 
 * Generate guesses of table column meaning based on data type of column
 * 
 * Generate guesses of table column meaning based on name and datatype of column
 * 
 * For column names, Look for keywords and variations:
 * 
 * To, From, Sender, Receiver, Name, *id, Date
 * 
 * Look for columns that don't match keywords, treat them as part of a payload
 * (Or maybe just treat the entire row as the payload, repeating what matched
 * the keywords)
 * 
 * For column datatypes:
 * 
 * 
 * @author djue
 * 
 */
public class Introspector {

	private DBConnectionPoolService cp;

	public Introspector(DBConnectionPoolService mainDB, SQLTemplates dialect) {
		this.cp = mainDB;
		this.dialect = dialect;
	}

	private static Logger logger = LoggerFactory.getLogger(Introspector.class);

	private final Set<String> classes = new HashSet<String>();

	public File getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(File targetFolder) {
		this.targetFolder = targetFolder;
	}

	public String getBeanPackageName() {
		return beanPackageName;
	}

	public void setBeanPackageName(String beanPackageName) {
		this.beanPackageName = beanPackageName;
	}

	private File targetFolder;

	@Nullable
	private String beanPackageName;
	private Map<String, Class> queryClasses = new HashMap<String, Class>(5);
	private Map<String, Class> domainClasses = new HashMap<String, Class>(5);

	public void introspect() {
		// TODO Auto-generated method stub
		ClassPath classPath = null;
		System.out.println("Starting introspection");
		try {
			classPath = ClassPath.from(Introspector.class.getClassLoader());
			System.out.println("Starting introspection for "+classPath.toString());
			for (ClassPath.ClassInfo classInfo : classPath
					.getTopLevelClassesRecursive(beanPackageName)) {
				Class<?> c = classInfo.load();
				System.out.println("Loading fields for class "
						+ c.getSimpleName());
				for (Field f : c.getFields()) {
					if (!Modifier.isStatic(f.getModifiers())) {
						System.out.println(c.getName() + "." + f.getName());
					}
				}
				if (c.getSimpleName().startsWith("Q")) {
					queryClasses.put(c.getSimpleName(), c);
				} else {
					domainClasses.put(c.getSimpleName(), c);
				}
				// learnAboutClass(c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String s : domainClasses.keySet()) {
			Class q = queryClasses.get("Q" + s);
			if (q != null) {

				learnAbout(domainClasses.get(s), q);
			} else {
				// tablename might start with a q?
				logger.error("Could not find query class for " + s);
			}
		}

	}

	private SQLTemplates dialect = new HSQLDBTemplates(); // SQL-dialect

	public DBConnectionPoolService getCp() {
		return cp;
	}

	public void setCp(DBConnectionPoolService cp) {
		this.cp = cp;
	}

	public SQLTemplates getDialect() {
		return dialect;
	}

	public void setDialect(SQLTemplates dialect) {
		this.dialect = dialect;
	}

	protected Connection getConnection() throws Exception {
		if (cp == null) {
			throw new Exception(
					"DBConnectionPoolService was not injected properly, check your bindings to make sure the registry knows how to supply this class.  Are you sure you didn't try to call new on the implementation?");
		}
		if (!cp.isInitialized()) {
			cp.init();
		}
		return cp.getConnection();
	}

	private void learnAbout(Class c, Class q) {
		// TODO Auto-generated method stub
		System.out.println("Learning about " + c.getSimpleName() + " and "
				+ q.getSimpleName());

		try {

			Constructor<? extends RelationalPathBase> ctor = q
					.getConstructor(String.class);

			RelationalPathBase qInstance = ctor.newInstance("t");
			System.out.println("========================================");
			System.out.println("== " + qInstance.getType().getCanonicalName());
			System.out.println("========================================");
			for (Object column : qInstance.getColumns()) {
				try {
					if (StringPath.class.isAssignableFrom(column.getClass())) {
						StringPath p = (StringPath) column;
						Connection conn = getConnection();
						SQLQuery sq = from(conn, qInstance);
						List results = sq.limit(10).list(p.min(), p.max(),
								p.count(), p.countDistinct());
						conn.close();
						System.out.println("Min, Max, Count, Count Distinct, for String column " + p
								+ ": " + results);

					} else if (NumberPath.class.isAssignableFrom(column
							.getClass())) {
						NumberPath p = (NumberPath) column;
						Connection conn = getConnection();
						SQLQuery sq = from(conn, qInstance);
						List results = sq.limit(10).list(p.min(), p.avg(),
								p.max(), p.count(), p.countDistinct());
						conn.close();
						System.out.println("Min, Max, Count, Count Distinct, for Numeric column " + p
								+ ": " + results);
					}

				} catch (Exception e) {
					logger.error("Error when learning about table "
							+ qInstance.getType().getCanonicalName()
							+ " on column " + column + ": "
							+ ExceptionUtil.getRootCauseMessage(e));
					//e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected SQLQuery from(Connection conn, EntityPath<?>... o) {
		return new SQLQuery(conn, dialect).from(o);
	}
}

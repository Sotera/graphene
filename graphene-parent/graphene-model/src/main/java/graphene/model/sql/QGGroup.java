package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QGGroup is a Querydsl query type for GGroup
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGGroup extends com.mysema.query.sql.RelationalPathBase<GGroup> {

    private static final long serialVersionUID = 938652722;

    public static final QGGroup gGroup = new QGGroup("G_GROUP");

    public final StringPath groupname = createString("groupname");

    public final com.mysema.query.sql.PrimaryKey<GGroup> _xfGroup_ED1647CD0FEC5ADDPK = createPrimaryKey(groupname);

    public final com.mysema.query.sql.ForeignKey<GUserGroups> __xfUserG_group_178D7CA5FK = createInvForeignKey(groupname, "groupname");

    public QGGroup(String variable) {
        super(GGroup.class,  forVariable(variable), "dbo", "G_GROUP");
        addMetadata();
    }

    public QGGroup(Path<? extends GGroup> path) {
        super(path.getType(), path.getMetadata(), "dbo", "G_GROUP");
        addMetadata();
    }

    public QGGroup(PathMetadata<?> metadata) {
        super(GGroup.class,  metadata, "dbo", "G_GROUP");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(groupname, ColumnMetadata.named("groupname").ofType(12).withSize(1).notNull());
    }

}


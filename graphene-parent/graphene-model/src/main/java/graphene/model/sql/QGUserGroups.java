package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QGUserGroups is a Querydsl query type for GUserGroups
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGUserGroups extends com.mysema.query.sql.RelationalPathBase<GUserGroups> {

    private static final long serialVersionUID = 1883129772;

    public static final QGUserGroups gUserGroups = new QGUserGroups("G_USER_GROUPS");

    public final StringPath groupname = createString("groupname");

    public final StringPath username = createString("username");

    public final com.mysema.query.sql.ForeignKey<GUser> _xfUserG_usern_1699586CFK = createForeignKey(username, "username");

    public final com.mysema.query.sql.ForeignKey<GGroup> _xfUserG_group_178D7CA5FK = createForeignKey(groupname, "groupname");

    public QGUserGroups(String variable) {
        super(GUserGroups.class,  forVariable(variable), "dbo", "G_USER_GROUPS");
        addMetadata();
    }

    public QGUserGroups(Path<? extends GUserGroups> path) {
        super(path.getType(), path.getMetadata(), "dbo", "G_USER_GROUPS");
        addMetadata();
    }

    public QGUserGroups(PathMetadata<?> metadata) {
        super(GUserGroups.class,  metadata, "dbo", "G_USER_GROUPS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(groupname, ColumnMetadata.named("groupname").ofType(12).withSize(1));
        addMetadata(username, ColumnMetadata.named("username").ofType(-9).withSize(100));
    }

}


package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QGUser is a Querydsl query type for GUser
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGUser extends com.mysema.query.sql.RelationalPathBase<GUser> {

    private static final long serialVersionUID = 1693264824;

    public static final QGUser gUser = new QGUser("G_USER");

    public final DateTimePath<java.sql.Timestamp> accountcreated = createDateTime("accountcreated", java.sql.Timestamp.class);

    public final BooleanPath active = createBoolean("active");

    public final StringPath avatar = createString("avatar");

    public final StringPath email = createString("email");

    public final StringPath fullname = createString("fullname");

    public final StringPath hashedpassword = createString("hashedpassword");

    public final DateTimePath<java.sql.Timestamp> lastlogin = createDateTime("lastlogin", java.sql.Timestamp.class);

    public final NumberPath<Integer> numberlogins = createNumber("numberlogins", Integer.class);

    public final StringPath salt = createString("salt");

    public final StringPath username = createString("username");

    public final com.mysema.query.sql.PrimaryKey<GUser> _xfUser_F3DBC5730C1BC9F9PK = createPrimaryKey(username);

    public final com.mysema.query.sql.ForeignKey<GUserGroups> __xfUserG_usern_1699586CFK = createInvForeignKey(username, "username");

    public final com.mysema.query.sql.ForeignKey<GUserWorkspaces> __xfUserW_usern_1975C517FK = createInvForeignKey(username, "username");

    public QGUser(String variable) {
        super(GUser.class,  forVariable(variable), "dbo", "G_USER");
        addMetadata();
    }

    public QGUser(Path<? extends GUser> path) {
        super(path.getType(), path.getMetadata(), "dbo", "G_USER");
        addMetadata();
    }

    public QGUser(PathMetadata<?> metadata) {
        super(GUser.class,  metadata, "dbo", "G_USER");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(accountcreated, ColumnMetadata.named("accountcreated").ofType(93).withSize(23).withDigits(3));
        addMetadata(active, ColumnMetadata.named("active").ofType(-7).withSize(1));
        addMetadata(avatar, ColumnMetadata.named("avatar").ofType(-9).withSize(200));
        addMetadata(email, ColumnMetadata.named("email").ofType(-9).withSize(200));
        addMetadata(fullname, ColumnMetadata.named("fullname").ofType(-9).withSize(200));
        addMetadata(hashedpassword, ColumnMetadata.named("hashedpassword").ofType(-9).withSize(256));
        addMetadata(lastlogin, ColumnMetadata.named("lastlogin").ofType(93).withSize(23).withDigits(3));
        addMetadata(numberlogins, ColumnMetadata.named("numberlogins").ofType(4).withSize(10));
        addMetadata(salt, ColumnMetadata.named("salt").ofType(-9).withSize(256));
        addMetadata(username, ColumnMetadata.named("username").ofType(-9).withSize(100).notNull());
    }

}


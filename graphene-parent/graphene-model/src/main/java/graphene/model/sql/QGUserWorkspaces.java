package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QGUserWorkspaces is a Querydsl query type for GUserWorkspaces
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGUserWorkspaces extends com.mysema.query.sql.RelationalPathBase<GUserWorkspaces> {

    private static final long serialVersionUID = -959748010;

    public static final QGUserWorkspaces gUserWorkspaces = new QGUserWorkspaces("G_USER_WORKSPACES");

    public final StringPath username = createString("username");

    public final NumberPath<Integer> workspaceId = createNumber("workspaceId", Integer.class);

    public final com.mysema.query.sql.ForeignKey<GUser> _xfUserW_usern_1975C517FK = createForeignKey(username, "username");

    public final com.mysema.query.sql.ForeignKey<GWorkspace> _xfUserW_works_1A69E950FK = createForeignKey(workspaceId, "workspace_id");

    public QGUserWorkspaces(String variable) {
        super(GUserWorkspaces.class,  forVariable(variable), "dbo", "G_USER_WORKSPACES");
        addMetadata();
    }

    public QGUserWorkspaces(Path<? extends GUserWorkspaces> path) {
        super(path.getType(), path.getMetadata(), "dbo", "G_USER_WORKSPACES");
        addMetadata();
    }

    public QGUserWorkspaces(PathMetadata<?> metadata) {
        super(GUserWorkspaces.class,  metadata, "dbo", "G_USER_WORKSPACES");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(username, ColumnMetadata.named("username").ofType(-9).withSize(100));
        addMetadata(workspaceId, ColumnMetadata.named("workspace_id").ofType(4).withSize(10));
    }

}


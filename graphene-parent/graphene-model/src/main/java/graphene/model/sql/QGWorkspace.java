package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QGWorkspace is a Querydsl query type for GWorkspace
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGWorkspace extends com.mysema.query.sql.RelationalPathBase<GWorkspace> {

    private static final long serialVersionUID = 1960679624;

    public static final QGWorkspace gWorkspace = new QGWorkspace("G_WORKSPACE");

    public final StringPath creatorUsername = createString("creatorUsername");

    public final StringPath json = createString("json");

    public final StringPath title = createString("title");

    public final NumberPath<Integer> workspaceId = createNumber("workspaceId", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<GWorkspace> _xfWorks_7C58AC0B13BCEBC1PK = createPrimaryKey(workspaceId);

    public final com.mysema.query.sql.ForeignKey<GUserWorkspaces> __xfUserW_works_1A69E950FK = createInvForeignKey(workspaceId, "workspace_id");

    public QGWorkspace(String variable) {
        super(GWorkspace.class,  forVariable(variable), "dbo", "G_WORKSPACE");
        addMetadata();
    }

    public QGWorkspace(Path<? extends GWorkspace> path) {
        super(path.getType(), path.getMetadata(), "dbo", "G_WORKSPACE");
        addMetadata();
    }

    public QGWorkspace(PathMetadata<?> metadata) {
        super(GWorkspace.class,  metadata, "dbo", "G_WORKSPACE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(creatorUsername, ColumnMetadata.named("creator_username").ofType(-9).withSize(100));
        addMetadata(json, ColumnMetadata.named("json").ofType(-9).withSize(2147483647));
        addMetadata(title, ColumnMetadata.named("title").ofType(-9).withSize(200));
        addMetadata(workspaceId, ColumnMetadata.named("workspace_id").ofType(4).withSize(10).notNull());
    }

}


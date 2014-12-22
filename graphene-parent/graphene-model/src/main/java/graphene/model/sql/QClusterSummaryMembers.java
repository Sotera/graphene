package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.StringPath;


/**
 * QClusterSummaryMembers is a Querydsl query type for ClusterSummaryMembers
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClusterSummaryMembers extends com.mysema.query.sql.RelationalPathBase<ClusterSummaryMembers> {

    private static final long serialVersionUID = 1034591763;

    public static final QClusterSummaryMembers ClusterSummaryMembers = new QClusterSummaryMembers("ClusterSummaryMembers");

    public final StringPath entityId = createString("entityId");

    public final StringPath summaryId = createString("summaryId");

    public QClusterSummaryMembers(String variable) {
        super(ClusterSummaryMembers.class,  forVariable(variable), "dbo", "ClusterSummaryMembers");
        addMetadata();
    }

    public QClusterSummaryMembers(Path<? extends ClusterSummaryMembers> path) {
        super(path.getType(), path.getMetadata(), "dbo", "ClusterSummaryMembers");
        addMetadata();
    }

    public QClusterSummaryMembers(PathMetadata<?> metadata) {
        super(ClusterSummaryMembers.class,  metadata, "dbo", "ClusterSummaryMembers");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(entityId, ColumnMetadata.named("EntityId").ofType(12).withSize(100));
        addMetadata(summaryId, ColumnMetadata.named("SummaryId").ofType(12).withSize(100));
    }

}


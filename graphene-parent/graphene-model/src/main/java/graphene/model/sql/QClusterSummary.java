package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QClusterSummary is a Querydsl query type for ClusterSummary
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QClusterSummary extends com.mysema.query.sql.RelationalPathBase<ClusterSummary> {

    private static final long serialVersionUID = 2030562342;

    public static final QClusterSummary ClusterSummary = new QClusterSummary("ClusterSummary");

    public final StringPath entityId = createString("entityId");

    public final StringPath property = createString("property");

    public final NumberPath<Double> stat = createNumber("stat", Double.class);

    public final StringPath tag = createString("tag");

    public final StringPath type = createString("type");

    public final StringPath value = createString("value");

    public QClusterSummary(String variable) {
        super(ClusterSummary.class,  forVariable(variable), "dbo", "ClusterSummary");
        addMetadata();
    }

    public QClusterSummary(Path<? extends ClusterSummary> path) {
        super(path.getType(), path.getMetadata(), "dbo", "ClusterSummary");
        addMetadata();
    }

    public QClusterSummary(PathMetadata<?> metadata) {
        super(ClusterSummary.class,  metadata, "dbo", "ClusterSummary");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(entityId, ColumnMetadata.named("EntityId").ofType(12).withSize(100));
        addMetadata(property, ColumnMetadata.named("Property").ofType(12).withSize(50));
        addMetadata(stat, ColumnMetadata.named("Stat").ofType(8).withSize(53));
        addMetadata(tag, ColumnMetadata.named("Tag").ofType(12).withSize(50));
        addMetadata(type, ColumnMetadata.named("Type").ofType(12).withSize(50));
        addMetadata(value, ColumnMetadata.named("Value").ofType(12).withSize(200));
    }

}


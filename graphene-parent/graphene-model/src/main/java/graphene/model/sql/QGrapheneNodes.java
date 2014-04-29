package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QGrapheneNodes is a Querydsl query type for GrapheneNodes
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGrapheneNodes extends com.mysema.query.sql.RelationalPathBase<GrapheneNodes> {

    private static final long serialVersionUID = -1159370903;

    public static final QGrapheneNodes grapheneNodes = new QGrapheneNodes("GRAPHENE_NODES");

    public final StringPath customerNumber1 = createString("customerNumber1");

    public final StringPath customerSourceTable = createString("customerSourceTable");

    public final NumberPath<Long> nodeId = createNumber("nodeId", Long.class);

    public QGrapheneNodes(String variable) {
        super(GrapheneNodes.class,  forVariable(variable), "dbo", "GRAPHENE_NODES");
        addMetadata();
    }

    public QGrapheneNodes(Path<? extends GrapheneNodes> path) {
        super(path.getType(), path.getMetadata(), "dbo", "GRAPHENE_NODES");
        addMetadata();
    }

    public QGrapheneNodes(PathMetadata<?> metadata) {
        super(GrapheneNodes.class,  metadata, "dbo", "GRAPHENE_NODES");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(customerNumber1, ColumnMetadata.named("CustomerNumber1").ofType(12).withSize(30));
        addMetadata(customerSourceTable, ColumnMetadata.named("CustomerSourceTable").ofType(12).withSize(30));
        addMetadata(nodeId, ColumnMetadata.named("node_id").ofType(-5).withSize(19));
    }

}


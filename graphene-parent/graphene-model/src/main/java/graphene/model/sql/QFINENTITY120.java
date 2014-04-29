package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QFinEntity120 is a Querydsl query type for FinEntity120
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFINENTITY120 extends com.mysema.query.sql.RelationalPathBase<FINENTITY120> {

    private static final long serialVersionUID = -2120657413;

    public static final QFINENTITY120 finEntity120 = new QFINENTITY120("FIN_ENTITY_1_20");

    public final StringPath entityId = createString("entityId");

    public final NumberPath<Integer> inboundDegree = createNumber("inboundDegree", Integer.class);

    public final NumberPath<Integer> outboundDegree = createNumber("outboundDegree", Integer.class);

    public final NumberPath<Integer> uniqueInboundDegree = createNumber("uniqueInboundDegree", Integer.class);

    public final NumberPath<Integer> uniqueOutboundDegree = createNumber("uniqueOutboundDegree", Integer.class);

    public QFINENTITY120(String variable) {
        super(FINENTITY120.class,  forVariable(variable), "dbo", "FIN_ENTITY_1_20");
        addMetadata();
    }

    public QFINENTITY120(Path<? extends FINENTITY120> path) {
        super(path.getType(), path.getMetadata(), "dbo", "FIN_ENTITY_1_20");
        addMetadata();
    }

    public QFINENTITY120(PathMetadata<?> metadata) {
        super(FINENTITY120.class,  metadata, "dbo", "FIN_ENTITY_1_20");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(entityId, ColumnMetadata.named("EntityId").ofType(12).withSize(100));
        addMetadata(inboundDegree, ColumnMetadata.named("InboundDegree").ofType(4).withSize(10));
        addMetadata(outboundDegree, ColumnMetadata.named("OutboundDegree").ofType(4).withSize(10));
        addMetadata(uniqueInboundDegree, ColumnMetadata.named("UniqueInboundDegree").ofType(4).withSize(10));
        addMetadata(uniqueOutboundDegree, ColumnMetadata.named("UniqueOutboundDegree").ofType(4).withSize(10));
    }

}


package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;


/**
 * QFINENTITYDaily120 is a Querydsl query type for FINENTITYDaily120
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFINENTITYDaily120 extends com.mysema.query.sql.RelationalPathBase<FINENTITYDaily120> {

    private static final long serialVersionUID = 1058827562;

    public static final QFINENTITYDaily120 FINENTITYDaily120 = new QFINENTITYDaily120("FIN_ENTITY_Daily_1_20");

    public final NumberPath<Double> balance = createNumber("balance", Double.class);

    public final StringPath entityId = createString("entityId");

    public final NumberPath<Double> inboundAmount = createNumber("inboundAmount", Double.class);

    public final NumberPath<Integer> inboundDegree = createNumber("inboundDegree", Integer.class);

    public final NumberPath<Double> outboundAmount = createNumber("outboundAmount", Double.class);

    public final NumberPath<Integer> outboundDegree = createNumber("outboundDegree", Integer.class);

    public final DateTimePath<java.sql.Timestamp> periodDate = createDateTime("periodDate", java.sql.Timestamp.class);

    public QFINENTITYDaily120(String variable) {
        super(FINENTITYDaily120.class,  forVariable(variable), "dbo", "FIN_ENTITY_Daily_1_20");
        addMetadata();
    }

    public QFINENTITYDaily120(Path<? extends FINENTITYDaily120> path) {
        super(path.getType(), path.getMetadata(), "dbo", "FIN_ENTITY_Daily_1_20");
        addMetadata();
    }

    public QFINENTITYDaily120(PathMetadata<?> metadata) {
        super(FINENTITYDaily120.class,  metadata, "dbo", "FIN_ENTITY_Daily_1_20");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(balance, ColumnMetadata.named("Balance").ofType(8).withSize(53));
        addMetadata(entityId, ColumnMetadata.named("EntityId").ofType(12).withSize(100));
        addMetadata(inboundAmount, ColumnMetadata.named("InboundAmount").ofType(8).withSize(53));
        addMetadata(inboundDegree, ColumnMetadata.named("InboundDegree").ofType(4).withSize(10));
        addMetadata(outboundAmount, ColumnMetadata.named("OutboundAmount").ofType(8).withSize(53));
        addMetadata(outboundDegree, ColumnMetadata.named("OutboundDegree").ofType(4).withSize(10));
        addMetadata(periodDate, ColumnMetadata.named("PeriodDate").ofType(93).withSize(23).withDigits(3));
    }

}


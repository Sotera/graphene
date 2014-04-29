package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QFINFLOWQuarterly120 is a Querydsl query type for FINFLOWQuarterly120
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFINFLOWQuarterly120 extends com.mysema.query.sql.RelationalPathBase<FINFLOWQuarterly120> {

    private static final long serialVersionUID = 1812015381;

    public static final QFINFLOWQuarterly120 FINFLOWQuarterly120 = new QFINFLOWQuarterly120("FIN_FLOW_Quarterly_1_20");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final StringPath fromEntityId = createString("fromEntityId");

    public final StringPath fromEntityType = createString("fromEntityType");

    public final DateTimePath<java.sql.Timestamp> periodDate = createDateTime("periodDate", java.sql.Timestamp.class);

    public final StringPath toEntityId = createString("toEntityId");

    public final StringPath toEntityType = createString("toEntityType");

    public QFINFLOWQuarterly120(String variable) {
        super(FINFLOWQuarterly120.class,  forVariable(variable), "dbo", "FIN_FLOW_Quarterly_1_20");
        addMetadata();
    }

    public QFINFLOWQuarterly120(Path<? extends FINFLOWQuarterly120> path) {
        super(path.getType(), path.getMetadata(), "dbo", "FIN_FLOW_Quarterly_1_20");
        addMetadata();
    }

    public QFINFLOWQuarterly120(PathMetadata<?> metadata) {
        super(FINFLOWQuarterly120.class,  metadata, "dbo", "FIN_FLOW_Quarterly_1_20");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(amount, ColumnMetadata.named("Amount").ofType(8).withSize(53));
        addMetadata(fromEntityId, ColumnMetadata.named("FromEntityId").ofType(12).withSize(100));
        addMetadata(fromEntityType, ColumnMetadata.named("FromEntityType").ofType(12).withSize(1));
        addMetadata(periodDate, ColumnMetadata.named("PeriodDate").ofType(93).withSize(23).withDigits(3));
        addMetadata(toEntityId, ColumnMetadata.named("ToEntityId").ofType(12).withSize(100));
        addMetadata(toEntityType, ColumnMetadata.named("ToEntityType").ofType(12).withSize(1));
    }

}


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
 * QFINFLOWYearly120 is a Querydsl query type for FINFLOWYearly120
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFINFLOWYearly120 extends com.mysema.query.sql.RelationalPathBase<FINFLOWYearly120> {

    private static final long serialVersionUID = 593230086;

    public static final QFINFLOWYearly120 FINFLOWYearly120 = new QFINFLOWYearly120("FIN_FLOW_Yearly_1_20");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final StringPath fromEntityId = createString("fromEntityId");

    public final StringPath fromEntityType = createString("fromEntityType");

    public final DateTimePath<java.sql.Timestamp> periodDate = createDateTime("periodDate", java.sql.Timestamp.class);

    public final StringPath toEntityId = createString("toEntityId");

    public final StringPath toEntityType = createString("toEntityType");

    public QFINFLOWYearly120(String variable) {
        super(FINFLOWYearly120.class,  forVariable(variable), "dbo", "FIN_FLOW_Yearly_1_20");
        addMetadata();
    }

    public QFINFLOWYearly120(Path<? extends FINFLOWYearly120> path) {
        super(path.getType(), path.getMetadata(), "dbo", "FIN_FLOW_Yearly_1_20");
        addMetadata();
    }

    public QFINFLOWYearly120(PathMetadata<?> metadata) {
        super(FINFLOWYearly120.class,  metadata, "dbo", "FIN_FLOW_Yearly_1_20");
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


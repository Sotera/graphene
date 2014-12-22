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
 * QFinFlow120 is a Querydsl query type for FinFlow120
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFINFLOW120 extends com.mysema.query.sql.RelationalPathBase<FINFLOW120> {

    private static final long serialVersionUID = 1869580432;

    public static final QFINFLOW120 finFlow120 = new QFINFLOW120("FIN_FLOW_1_20");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final DateTimePath<java.sql.Timestamp> firstTransaction = createDateTime("firstTransaction", java.sql.Timestamp.class);

    public final StringPath fromEntityId = createString("fromEntityId");

    public final StringPath fromEntityType = createString("fromEntityType");

    public final DateTimePath<java.sql.Timestamp> lastTransaction = createDateTime("lastTransaction", java.sql.Timestamp.class);

    public final StringPath toEntityId = createString("toEntityId");

    public final StringPath toEntityType = createString("toEntityType");

    public QFINFLOW120(String variable) {
        super(FINFLOW120.class,  forVariable(variable), "dbo", "FIN_FLOW_1_20");
        addMetadata();
    }

    public QFINFLOW120(Path<? extends FINFLOW120> path) {
        super(path.getType(), path.getMetadata(), "dbo", "FIN_FLOW_1_20");
        addMetadata();
    }

    public QFINFLOW120(PathMetadata<?> metadata) {
        super(FINFLOW120.class,  metadata, "dbo", "FIN_FLOW_1_20");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(amount, ColumnMetadata.named("Amount").ofType(8).withSize(53));
        addMetadata(firstTransaction, ColumnMetadata.named("FirstTransaction").ofType(93).withSize(23).withDigits(3));
        addMetadata(fromEntityId, ColumnMetadata.named("FromEntityId").ofType(12).withSize(100));
        addMetadata(fromEntityType, ColumnMetadata.named("FromEntityType").ofType(12).withSize(1));
        addMetadata(lastTransaction, ColumnMetadata.named("LastTransaction").ofType(93).withSize(23).withDigits(3));
        addMetadata(toEntityId, ColumnMetadata.named("ToEntityId").ofType(12).withSize(100));
        addMetadata(toEntityType, ColumnMetadata.named("ToEntityType").ofType(12).withSize(1));
    }

}


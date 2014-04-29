package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QDynamicClusterDataview100 is a Querydsl query type for DynamicClusterDataview100
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QDynamicClusterDataview100 extends com.mysema.query.sql.RelationalPathBase<DynamicClusterDataview100> {

    private static final long serialVersionUID = -1630287251;

    public static final QDynamicClusterDataview100 dynamicClusterDataview100 = new QDynamicClusterDataview100("DYNAMIC_CLUSTER_DATAVIEW_1_00");

    public final StringPath clusterid = createString("clusterid");

    public final StringPath contextid = createString("contextid");

    public final StringPath entityid = createString("entityid");

    public final StringPath globalclusterid = createString("globalclusterid");

    public final NumberPath<Integer> hierarchylevel = createNumber("hierarchylevel", Integer.class);

    public final DateTimePath<java.sql.Timestamp> modifiedDate = createDateTime("modifiedDate", java.sql.Timestamp.class);

    public final StringPath parentid = createString("parentid");

    public final StringPath rootid = createString("rootid");

    public QDynamicClusterDataview100(String variable) {
        super(DynamicClusterDataview100.class,  forVariable(variable), "dbo", "DYNAMIC_CLUSTER_DATAVIEW_1_00");
        addMetadata();
    }

    public QDynamicClusterDataview100(Path<? extends DynamicClusterDataview100> path) {
        super(path.getType(), path.getMetadata(), "dbo", "DYNAMIC_CLUSTER_DATAVIEW_1_00");
        addMetadata();
    }

    public QDynamicClusterDataview100(PathMetadata<?> metadata) {
        super(DynamicClusterDataview100.class,  metadata, "dbo", "DYNAMIC_CLUSTER_DATAVIEW_1_00");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clusterid, ColumnMetadata.named("clusterid").ofType(-9).withSize(402));
        addMetadata(contextid, ColumnMetadata.named("contextid").ofType(-9).withSize(200));
        addMetadata(entityid, ColumnMetadata.named("entityid").ofType(-9).withSize(200).notNull());
        addMetadata(globalclusterid, ColumnMetadata.named("globalclusterid").ofType(-9).withSize(200));
        addMetadata(hierarchylevel, ColumnMetadata.named("hierarchylevel").ofType(4).withSize(10).notNull());
        addMetadata(modifiedDate, ColumnMetadata.named("modifiedDate").ofType(93).withSize(23).withDigits(3).notNull());
        addMetadata(parentid, ColumnMetadata.named("parentid").ofType(-9).withSize(402));
        addMetadata(rootid, ColumnMetadata.named("rootid").ofType(-9).withSize(402));
    }

}


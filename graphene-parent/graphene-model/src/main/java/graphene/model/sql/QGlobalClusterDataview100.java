package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;


/**
 * QGlobalClusterDataview100 is a Querydsl query type for GlobalClusterDataview100
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGlobalClusterDataview100 extends com.mysema.query.sql.RelationalPathBase<GlobalClusterDataview100> {

    private static final long serialVersionUID = -1688897467;

    public static final QGlobalClusterDataview100 globalClusterDataview100 = new QGlobalClusterDataview100("GLOBAL_CLUSTER_DATAVIEW_1_00");

    public final StringPath clusterid = createString("clusterid");

    public final StringPath entityid = createString("entityid");

    public final NumberPath<Integer> hierarchylevel = createNumber("hierarchylevel", Integer.class);

    public final StringPath parentid = createString("parentid");

    public final StringPath rootid = createString("rootid");

    public QGlobalClusterDataview100(String variable) {
        super(GlobalClusterDataview100.class,  forVariable(variable), "dbo", "GLOBAL_CLUSTER_DATAVIEW_1_00");
        addMetadata();
    }

    public QGlobalClusterDataview100(Path<? extends GlobalClusterDataview100> path) {
        super(path.getType(), path.getMetadata(), "dbo", "GLOBAL_CLUSTER_DATAVIEW_1_00");
        addMetadata();
    }

    public QGlobalClusterDataview100(PathMetadata<?> metadata) {
        super(GlobalClusterDataview100.class,  metadata, "dbo", "GLOBAL_CLUSTER_DATAVIEW_1_00");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clusterid, ColumnMetadata.named("clusterid").ofType(-9).withSize(200));
        addMetadata(entityid, ColumnMetadata.named("entityid").ofType(-9).withSize(200).notNull());
        addMetadata(hierarchylevel, ColumnMetadata.named("hierarchylevel").ofType(4).withSize(10).notNull());
        addMetadata(parentid, ColumnMetadata.named("parentid").ofType(-9).withSize(200));
        addMetadata(rootid, ColumnMetadata.named("rootid").ofType(-9).withSize(200));
    }

}


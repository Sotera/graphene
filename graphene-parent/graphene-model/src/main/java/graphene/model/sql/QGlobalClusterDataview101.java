package graphene.model.sql;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;


/**
 * QGlobalClusterDataview101 is a Querydsl query type for GlobalClusterDataview101
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGlobalClusterDataview101 extends com.mysema.query.sql.RelationalPathBase<GlobalClusterDataview101> {

    private static final long serialVersionUID = -1688897466;

    public static final QGlobalClusterDataview101 globalClusterDataview101 = new QGlobalClusterDataview101("GLOBAL_CLUSTER_DATAVIEW_1_01");

    public final StringPath clusterid = createString("clusterid");

    public final StringPath entityid = createString("entityid");

    public final NumberPath<Integer> hierarchylevel = createNumber("hierarchylevel", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath isleaf = createString("isleaf");

    public final StringPath parentid = createString("parentid");

    public final StringPath rootid = createString("rootid");

    public final com.mysema.query.sql.PrimaryKey<GlobalClusterDataview101> _globalC_3213e83f2c738af2Pk = createPrimaryKey(id);

    public QGlobalClusterDataview101(String variable) {
        super(GlobalClusterDataview101.class,  forVariable(variable), "dbo", "GLOBAL_CLUSTER_DATAVIEW_1_01");
        addMetadata();
    }

    public QGlobalClusterDataview101(Path<? extends GlobalClusterDataview101> path) {
        super(path.getType(), path.getMetadata(), "dbo", "GLOBAL_CLUSTER_DATAVIEW_1_01");
        addMetadata();
    }

    public QGlobalClusterDataview101(PathMetadata<?> metadata) {
        super(GlobalClusterDataview101.class,  metadata, "dbo", "GLOBAL_CLUSTER_DATAVIEW_1_01");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(clusterid, ColumnMetadata.named("clusterid").ofType(-9).withSize(200));
        addMetadata(entityid, ColumnMetadata.named("entityid").ofType(-9).withSize(200).notNull());
        addMetadata(hierarchylevel, ColumnMetadata.named("hierarchylevel").ofType(4).withSize(10).notNull());
        addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull());
        addMetadata(isleaf, ColumnMetadata.named("isleaf").ofType(-9).withSize(2).notNull());
        addMetadata(parentid, ColumnMetadata.named("parentid").ofType(-9).withSize(200));
        addMetadata(rootid, ColumnMetadata.named("rootid").ofType(-9).withSize(200));
    }

}


create table dataset_meta_info
(
    id           bigint auto_increment
        primary key,
    dataset_type varchar(56) not null,
    meta_type    varchar(56) not null,
    var_name     varchar(56) not null,
    meta_var     varchar(56) not null,
    default_unit varchar(56) null,
    support_unit varchar(56) null,
    var_desc     text        null
)
    comment '天气数据集元数据信息';
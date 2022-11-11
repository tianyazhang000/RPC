drop table tb_user if exists;

create table tb_user(
    id integer not null auto_increment,
    name varchar(32),
    age int(3),
    gender varchar(8),
    primary key (id)
);

insert into tb_user(id,name,age,gender) values
                                                (default,'张三',20,'男'),
                                                (default,'李四',21,'男'),
                                                (default,'王五',27,'男'),
                                                (default,'赵六',18,'男'),
                                                (default,'钱七',30,'男'),
                                                (default,'刘八',40,'男');
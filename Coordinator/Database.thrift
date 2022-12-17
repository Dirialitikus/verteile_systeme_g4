namespace java thrift.database.crud.pca

typedef i32 int

struct Dataset{
    1: int primaryKey;
    2: int sensorID;
    3: string valueType;
    4: int sensorValue;
    5: string timestamp;
}

service CrudPcaService{
    void create(1: Dataset newDataset);
    Dataset read(1: int primaryKey);
    void update(1: Dataset updatedDataSet);
    void remove(1: int primaryKey);

    bool prepare(1: Dataset newDataset);
    void commit(1: int primaryKey);
    void abort(1: int primaryKey);
}
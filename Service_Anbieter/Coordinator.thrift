namespace java thrift.coordinator.commit

typedef i32 int

struct Dataset{
    1: int primaryKey;
    2: int sensorID;
    3: string valueType;
    4: int sensorValue;
    5: string timestamp;
}

service crudService{
     void create(1: Dataset newDataset);
     Dataset read(1: int primaryKey);
     void update(1: Dataset updatedDataSet);
     void remove(1: int primaryKey);
}
package models;

import java.sql.ResultSet;
import java.util.Map;

public abstract class Model {
    public abstract String getPK();
    public abstract void setPK(String pk);
    public abstract String getInsertStatement();
    public abstract String getTableName();
    public abstract Map<String,String> getValues();
    public abstract void setInfo(Map<String,String> info);
}

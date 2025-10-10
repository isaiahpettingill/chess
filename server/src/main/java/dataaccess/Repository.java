package dataaccess;

import java.sql.Blob;
import java.sql.ResultSet;
import java.util.Optional;

import models.Model;

abstract class Repository<T extends Model> {
    protected Optional<ResultSet> query(String query, Object[] params, String[] columnNames){
        try {
            var connection = DatabaseManager.getConnection();  
            var statement = connection.prepareStatement(query, columnNames);
            // for (int i = 0; i < params.length; i++){
            //     if (params[i] instanceof String val){
            //         statement.setString(i, val);
            //     }
            //     else if (params[i] instanceof Integer val){
            //         statement.setInt(i, val);
            //     }
            //     else if (params[i] instanceof Long val){
            //         statement.setLong(i, val);
            //     }
            //     else if (params[i] instanceof Blob val){
            //         statement.setBlob(i, val);
            //     }
            //     else if (params[i] instanceof Boolean val){
            //         statement.setBoolean(i, val);
            //     }
            //     else if (params[i] instanceof Double val){
            //         statement.setDouble(i, val);
            //     }
            //     else if (params[i] instanceof Float val){
            //         statement.setFloat(i, val);
            //     }
            //     else if (params[i] == null){
            //         statement.setNull(i, );
            //     }
            //     else {
            //         throw new RuntimeException("Param " + i + " is not of a supported type. Type: " + params[i].getClass().getName());
            //     }
            // }
            var results = statement.executeQuery();
            connection.close();
            return Optional.of(results);
        }
        catch (Exception e){
            return Optional.empty();
        }
    }

    // protected Optional<T> getById(int id) {
        
    // }
}

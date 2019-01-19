package de.uni_potsdam.hpi.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CalciteDataAccessObject extends DataAccessObject {

	@Override
	public String getDriverClassName() {
		return "org.apache.calcite.jdbc.Driver";
	}

	@Override
	public String limitSuffix(int limit) {
		if (limit <= 0)
			return "";
		return " LIMIT " + limit;
	}
	
	@Override
	public PreparedStatement insertValuesIntoStatement(PreparedStatement statement, String[] values, String[] valueTypes, int offset) throws NumberFormatException, SQLException {
		for (int i = 0; i < values.length; i++)
			statement.setString(i + 1, values[i]);
		return statement;
	}

	@Override
	public String buildSelectDistinctSortedStringColumnQuery(String tableName, String attributeName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String buildSelectDistinctSortedNumberColumnQuery(String tableName, String attributeName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String buildSelectDistinctSortedBinaryColumnQuery(String tableName, String attributeName) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String buildCreateIndexQuery(String tableName, String attributeName) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String buildDropIndexQuery(String tableName, String attributeName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String buildColumnMetaQuery(String databaseName, String tableName) {
		return "SELECT DISTINCT \"columnName\", \"typeName\", \"ordinalPosition\" " +
			   "FROM \"metadata\".\"COLUMNS\" " +
			   "WHERE LOWER(\"tableName\") = LOWER('" + tableName.replaceAll("\"$|^\"", "") + "') " +
			   "AND LOWER(\"tableSchem\") = LOWER('" + databaseName + "') " +
			   "ORDER BY \"ordinalPosition\" ASC";
	}

	@Override
	public String buildTableQuery(String databaseName) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void extract(List<String> names, List<String> types, List<String> basicTypes, ResultSet columnsResultSet) throws SQLException {
		while (columnsResultSet.next()) {
			names.add(columnsResultSet.getString("columnName"));
			
			String type = columnsResultSet.getString("typeName");
            types.add(type);
			basicTypes.add(type);
		}
	}
}

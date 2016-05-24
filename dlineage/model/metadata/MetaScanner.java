
package demos.dlineage.model.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import demos.dlineage.Dlineage;

public class MetaScanner
{

	private Dlineage dlineage;

	public MetaScanner( Dlineage dlineage )
	{
		this.dlineage = dlineage;
	}

	public ColumnMetaData getColumnMetaData( String columnFullName )
	{
		List<String> segments = parseNames( columnFullName );

		String database = null;
		String tableSchema = null;
		String tableName = null;
		String columnName = null;

		if ( segments.size( ) == 4 )
		{
			database = segments.get( 0 );
			segments.remove( 0 );
		}

		if ( segments.size( ) == 3 )
		{
			tableSchema = segments.get( 0 );
			segments.remove( 0 );
		}

		if ( segments.size( ) == 2 )
		{
			tableName = segments.get( 0 );
			segments.remove( 0 );
		}

		columnName = segments.get( 0 );

		return getColumnMetaData( getTableMetaData( database,
				tableSchema,
				tableName ),
				columnName );
	}

	public ColumnMetaData ColumnMetaData( String database, String tableSchema,
			String tableName, String columnName )
	{
		return getColumnMetaData( getTableMetaData( database,
				tableSchema,
				tableName ),
				columnName );
	}

	public TableMetaData getTableMetaData( String database, String tableSchema,
			String tableName )
	{
		TableMetaData tableMetaData = new TableMetaData( dlineage.getVendor( ),
				dlineage.isStrict( ) );
		tableMetaData.setName( tableName );
		tableMetaData.setSchemaName( tableSchema );
		if ( database != null )
			tableMetaData.setCatalogName( database );
		tableMetaData = getTableMetaData( tableMetaData );
		return tableMetaData;
	}

	public ColumnMetaData getColumnMetaData( TableMetaData tableMetaData,
			String columnName )
	{
		ColumnMetaData columnMetaData = new ColumnMetaData( );
		columnMetaData.setName( columnName );
		columnMetaData.setTable( tableMetaData );

		if ( dlineage.getMetaData( ).get( tableMetaData ) == null )
			return null;
		int index = dlineage.getMetaData( )
				.get( tableMetaData )
				.indexOf( columnMetaData );
		if ( index != -1 )
		{
			columnMetaData = dlineage.getMetaData( )
					.get( tableMetaData )
					.get( index );
		}
		else
		{
			return null;
		}
		return columnMetaData;
	}

	private TableMetaData getTableMetaData( TableMetaData tableMetaData )
	{
		List<TableMetaData> tables = Arrays.asList( dlineage.getMetaData( )
				.keySet( )
				.toArray( new TableMetaData[0] ) );
		int index = tables.indexOf( tableMetaData );
		if ( index != -1 )
		{
			return tables.get( index );
		}
		else
		{
			return null;
		}
	}

	private static List<String> parseNames( String nameString )
	{
		String[] splits = nameString.toUpperCase( ).split( "\\." );
		List<String> names = new ArrayList<String>( );
		for ( int i = 0; i < splits.length; i++ )
		{
			String split = splits[i].trim( );
			if ( split.startsWith( "[" ) && !split.endsWith( "]" ) )
			{
				StringBuffer buffer = new StringBuffer( );
				buffer.append( split );
				while ( !( split = splits[++i].trim( ) ).endsWith( "]" ) )
				{
					buffer.append( "." );
					buffer.append( split );
				}

				buffer.append( "." );
				buffer.append( split );

				names.add( buffer.toString( ) );
				continue;
			}
			if ( split.startsWith( "\"" ) && !split.endsWith( "\"" ) )
			{
				StringBuffer buffer = new StringBuffer( );
				buffer.append( split );
				while ( !( split = splits[++i].trim( ) ).endsWith( "\"" ) )
				{
					buffer.append( "." );
					buffer.append( split );
				}

				buffer.append( "." );
				buffer.append( split );

				names.add( buffer.toString( ) );
				continue;
			}
			names.add( split );
		}
		return names;
	}

	public ColumnMetaData getColumnMetaData( String tableFullName, String columnName )
	{
		List<String> segments = parseNames( tableFullName );

		String database = null;
		String tableSchema = null;
		String tableName = null;

		if ( segments.size( ) == 3 )
		{
			database = segments.get( 0 );
			segments.remove( 0 );
		}

		if ( segments.size( ) == 2 )
		{
			tableSchema = segments.get( 0 );
			segments.remove( 0 );
		}

		if ( segments.size( ) == 1 )
		{
			tableName = segments.get( 0 );
			segments.remove( 0 );
		}
		
		return getColumnMetaData( getTableMetaData( database,
				tableSchema,
				tableName ),
				columnName );
	}
}

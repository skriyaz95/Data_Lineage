
package demos.dlineage;

import gudusoft.gsqlparser.EDbVendor;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import demos.dlineage.columnImpact.ColumnImpact;
import demos.dlineage.metadata.DDLParser;
import demos.dlineage.metadata.DDLSchema;
import demos.dlineage.metadata.ProcedureRelationScanner;
import demos.dlineage.metadata.ViewParser;
import demos.dlineage.model.ddl.schema.database;
import demos.dlineage.model.metadata.ColumnMetaData;
import demos.dlineage.model.metadata.MetaScanner;
import demos.dlineage.model.metadata.ProcedureMetaData;
import demos.dlineage.model.metadata.TableMetaData;
import demos.dlineage.model.xml.columnImpactResult;
import demos.dlineage.model.xml.procedureImpactResult;
import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;

public class Dlineage
{

	private Map<TableMetaData, List<ColumnMetaData>> tableColumns = new HashMap<TableMetaData, List<ColumnMetaData>>( );
	private Pair<procedureImpactResult, List<ProcedureMetaData>> procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
			new ArrayList<ProcedureMetaData>( ) );

	private boolean strict = false;
	private boolean showUIInfo = false;
	private File sqlDir;
	private File[] sqlFiles;
	private String sqlContent;
	private EDbVendor vendor;

	public Dlineage( String sqlContent, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.vendor = vendor;
		this.sqlFiles = null;
		this.sqlContent = sqlContent;
		tableColumns.clear( );
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );

		String content = sqlContent;
		String database = null;

		database = new DDLParser( tableColumns,
				procedures,
				vendor,
				content.toUpperCase( ),
				strict,
				database ).getDatabase( );

		database = new ViewParser( tableColumns,
				vendor,
				content.toUpperCase( ),
				strict,
				database ).getDatabase( );

		database = new ProcedureRelationScanner( procedures,
				vendor,
				content.toUpperCase( ),
				strict,
				database ).getDatabase( );
	}
	
	public Dlineage( String[] sqlContents, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.vendor = vendor;
		this.sqlFiles = null;
		tableColumns.clear( );
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );

		for ( int i = 0; i < sqlContents.length; i++ )
		{
			String content = sqlContents[i];
			String database = null;
			database = new DDLParser( tableColumns,
					procedures,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}

		String database = null;
		for ( int i = 0; i < sqlContents.length; i++ )
		{
			String content = sqlContents[i];
			database = new ViewParser( tableColumns,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}
		
		database = null;
		for ( int i = 0; i < sqlContents.length; i++ )
		{
			String content = sqlContents[i];
			database = new ProcedureRelationScanner( procedures,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}
	}

	public Dlineage( File[] sqlFiles, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.vendor = vendor;
		this.sqlFiles = sqlFiles;
		tableColumns.clear( );
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );
		File[] children = sqlFiles;

		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );
			String database = null;
			database = new DDLParser( tableColumns,
					procedures,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}

		String database = null;
		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );
			database = new ViewParser( tableColumns,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}
		
		database = null;
		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );
			database = new ProcedureRelationScanner( procedures,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}
	}

	public Dlineage( File sqlDir, EDbVendor vendor, boolean strict,
			boolean showUIInfo )
	{
		this.strict = strict;
		this.showUIInfo = showUIInfo;
		this.sqlDir = sqlDir;
		this.vendor = vendor;
		tableColumns.clear( );
		procedures = new Pair<procedureImpactResult, List<ProcedureMetaData>>( new procedureImpactResult( ),
				new ArrayList<ProcedureMetaData>( ) );
		File[] children = listFiles( sqlDir );

		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );

			String database = null;

			database = new DDLParser( tableColumns,
					procedures,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );

		}

		String database = null;
		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );

			database = new ViewParser( tableColumns,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );

		}
		
		database = null;
		for ( int i = 0; i < children.length; i++ )
		{
			File child = children[i];
			if ( child.isDirectory( ) )
				continue;
			String content = SQLUtil.getFileContent( child );
			
			database = new ProcedureRelationScanner( procedures,
					vendor,
					content.toUpperCase( ),
					strict,
					database ).getDatabase( );
		}
	}

	public void columnImpact( )
	{
		String result = getColumnImpactResult( false );
		System.out.println( result );
	}

	public String getColumnImpactResult( )
	{
		return getColumnImpactResult( true );
	}

	public String getColumnImpactResult( boolean analyzeDlineage )
	{
		if ( sqlContent == null )
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
			Document doc = null;
			Element columnImpactResult = null;
			try
			{
				DocumentBuilder builder = factory.newDocumentBuilder( );
				doc = builder.newDocument( );
				doc.setXmlVersion( "1.0" );
				columnImpactResult = doc.createElement( "columnImpactResult" );
				doc.appendChild( columnImpactResult );
				if ( sqlDir != null && sqlDir.isDirectory( ) )
				{
					Element dirNode = doc.createElement( "dir" );
					dirNode.setAttribute( "name", sqlDir.getAbsolutePath( ) );
					columnImpactResult.appendChild( dirNode );
				}
			}
			catch ( ParserConfigurationException e )
			{
				e.printStackTrace( );
			}

			File[] children = sqlFiles == null ? listFiles( sqlDir ) : sqlFiles;
			for ( int i = 0; i < children.length; i++ )
			{
				File child = children[i];
				if ( child.isDirectory( ) )
					continue;
				if ( child != null )
				{
					Element fileNode = doc.createElement( "file" );
					fileNode.setAttribute( "name", child.getAbsolutePath( ) );
					ColumnImpact impact = new ColumnImpact( fileNode,
							vendor,
							tableColumns,
							strict );
					impact.setDebug( false );
					impact.setShowUIInfo( showUIInfo );
					impact.setTraceErrorMessage( false );
					impact.setAnalyzeDlineage( analyzeDlineage );
					impact.ignoreTopSelect( false );
					impact.impactSQL( );
					if ( impact.getErrorMessage( ) != null
							&& impact.getErrorMessage( ).trim( ).length( ) > 0 )
					{
						System.err.println( impact.getErrorMessage( ).trim( ) );
					}
					if ( fileNode.hasChildNodes( ) )
					{
						columnImpactResult.appendChild( fileNode );
					}
				}
			}
			if ( doc != null )
			{
				try
				{
					StringWriter sw = new StringWriter( );
					com.sun.org.apache.xml.internal.serialize.OutputFormat format = new com.sun.org.apache.xml.internal.serialize.OutputFormat( doc );
					format.setIndenting( true );
					format.setIndent( 2 );
					format.setLineWidth( 0 );
					Writer output = new BufferedWriter( sw );
					com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer( output,
							format );
					serializer.serialize( doc );
					String result = sw.toString( ).trim( );
					sw.close( );
					return result;
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
		else
		{
			ColumnImpact impact = new ColumnImpact( sqlContent,
					vendor,
					tableColumns,
					strict );
			impact.setDebug( false );
			impact.setShowUIInfo( showUIInfo );
			impact.setTraceErrorMessage( false );
			impact.setAnalyzeDlineage( true );
			impact.impactSQL( );
			if ( impact.getErrorMessage( ) != null
					&& impact.getErrorMessage( ).trim( ).length( ) > 0 )
			{
				System.err.println( impact.getErrorMessage( ).trim( ) );
			}
			return impact.getImpactResult( );
		}
		return null;
	}

	public void forwardAnalyze( String tableColumn,
			List<ColumnMetaData[]> relations )
	{
		ColumnMetaData columnMetaData = new MetaScanner( this ).getColumnMetaData( tableColumn );
		List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>( );
		Iterator<TableMetaData> iter = tableColumns.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			columns.addAll( tableColumns.get( iter.next( ) ) );
		}
		if ( columnMetaData != null )
		{
			outputForwardAnalyze( columnMetaData, columns, 0, relations );
		}
	}

	public void backwardAnalyze( String viewColumn,
			List<ColumnMetaData[]> relations )
	{
		ColumnMetaData columnMetaData = new MetaScanner( this ).getColumnMetaData( viewColumn );
		if ( columnMetaData != null )
		{
			outputBackwardAnalyze( columnMetaData, 0, relations );
		}
	}

	private void outputBackwardAnalyze( ColumnMetaData columnMetaData,
			int level, List<ColumnMetaData[]> relations )
	{
		if ( level > 0 )
		{
			for ( int i = 0; i < level; i++ )
			{
				System.out.print( "---" );
			}
			System.out.print( ">" );
		}
		System.out.println( columnMetaData.getDisplayFullName( ) );
		if ( columnMetaData.getReferColumns( ) != null
				&& columnMetaData.getReferColumns( ).length > 0 )
		{
			for ( int i = 0; i < columnMetaData.getReferColumns( ).length; i++ )
			{
				ColumnMetaData sourceColumn = columnMetaData.getReferColumns( )[i];
				if ( containsRelation( columnMetaData, sourceColumn, relations ) )
				{
					outputBackwardAnalyze( columnMetaData.getReferColumns( )[i],
							level + 1,
							relations );
				}
			}
		}
	}

	private boolean containsRelation( ColumnMetaData targetColumn,
			ColumnMetaData sourceColumn, List<ColumnMetaData[]> relations )
	{
		if ( relations == null )
			return false;
		for ( int i = 0; i < relations.size( ); i++ )
		{
			ColumnMetaData[] relation = relations.get( i );
			if ( relation[0].equals( targetColumn )
					&& relation[1].equals( sourceColumn ) )
				return true;
		}
		return false;
	}

	private void outputForwardAnalyze( ColumnMetaData columnMetaData,
			List<ColumnMetaData> columns, int level,
			List<ColumnMetaData[]> relations )
	{
		if ( level > 0 )
		{
			for ( int i = 0; i < level; i++ )
			{
				System.out.print( "---" );
			}
			System.out.print( ">" );
		}
		System.out.println( columnMetaData.getDisplayFullName( ) );
		for ( int i = 0; i < columns.size( ); i++ )
		{
			ColumnMetaData targetColumn = columns.get( i );
			if ( Arrays.asList( targetColumn.getReferColumns( ) )
					.contains( columnMetaData ) )
			{
				if ( containsRelation( targetColumn, columnMetaData, relations ) )
				{
					outputForwardAnalyze( targetColumn,
							columns,
							level + 1,
							relations );
				}
			}
		}
	}

	public void outputDDLSchema( )
	{
		System.out.println( new DDLSchema( tableColumns ).getSchemaXML( ) );
	}

	public database[] getDataMetaInfos( )
	{
		return new DDLSchema( tableColumns ).getDataMetaInfos( );
	}

	private File[] listFiles( File sqlFiles )
	{
		List<File> children = new ArrayList<File>( );
		if ( sqlFiles != null )
			listFiles( sqlFiles, children );
		return children.toArray( new File[0] );
	}

	private void listFiles( File rootFile, List<File> children )
	{
		if ( rootFile.isFile( ) )
			children.add( rootFile );
		else
		{
			File[] files = rootFile.listFiles( );
			for ( int i = 0; i < files.length; i++ )
			{
				listFiles( files[i], children );
			}
		}
	}

	public static void main( String[] args )
	{
		if ( args.length < 1 )
		{
			System.out.println( "Usage: java Dlineage [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/t <database type>] [/fo <table column>] [/b <view column>] [/ddl] [/s] [/log]" );
			System.out.println( "/f: Option, specify the sql file path to analyze dlineage." );
			System.out.println( "/d: Option, specify the sql directory path to analyze dlineage." );
			System.out.println( "/d: Option, forward analyze the specified table column." );
			System.out.println( "/t: Option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle" );
			System.out.println( "/fo: Option, forward analyze the specified table column." );
			System.out.println( "/b: Option, backward analyze the specified view column." );
			System.out.println( "/ddl: Option, output the database DDL schema." );
			System.out.println( "/s: Option, set the strict match mode. It will match the catalog name and schema name." );
			System.out.println( "/log: Option, generate a dlineage.log file to log information." );
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList( args );

		if ( argList.indexOf( "/f" ) != -1
				&& argList.size( ) > argList.indexOf( "/f" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/f" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isFile( ) )
			{
				System.out.println( sqlFiles + " is not a valid file." );
				return;
			}
		}
		else if ( argList.indexOf( "/d" ) != -1
				&& argList.size( ) > argList.indexOf( "/d" ) + 1 )
		{
			sqlFiles = new File( args[argList.indexOf( "/d" ) + 1] );
			if ( !sqlFiles.exists( ) || !sqlFiles.isDirectory( ) )
			{
				System.out.println( sqlFiles + " is not a valid directory." );
				return;
			}
		}
		else
		{
			System.out.println( "Please specify a sql file path or directory path to analyze dlineage." );
			return;
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf( "/t" );

		if ( index != -1 && args.length > index + 1 )
		{
			if ( args[index + 1].equalsIgnoreCase( "mssql" ) )
			{
				vendor = EDbVendor.dbvmssql;
			}
			else if ( args[index + 1].equalsIgnoreCase( "db2" ) )
			{
				vendor = EDbVendor.dbvdb2;
			}
			else if ( args[index + 1].equalsIgnoreCase( "mysql" ) )
			{
				vendor = EDbVendor.dbvmysql;
			}
			else if ( args[index + 1].equalsIgnoreCase( "netezza" ) )
			{
				vendor = EDbVendor.dbvnetezza;
			}
			else if ( args[index + 1].equalsIgnoreCase( "teradata" ) )
			{
				vendor = EDbVendor.dbvteradata;
			}
			else if ( args[index + 1].equalsIgnoreCase( "oracle" ) )
			{
				vendor = EDbVendor.dbvoracle;
			}
			else if ( args[index + 1].equalsIgnoreCase( "informix" ) )
			{
				vendor = EDbVendor.dbvinformix;
			}
			else if ( args[index + 1].equalsIgnoreCase( "sybase" ) )
			{
				vendor = EDbVendor.dbvsybase;
			}
			else if ( args[index + 1].equalsIgnoreCase( "postgresql" ) )
			{
				vendor = EDbVendor.dbvpostgresql;
			}
			else if ( args[index + 1].equalsIgnoreCase( "hive" ) )
			{
				vendor = EDbVendor.dbvhive;
			}
			else if ( args[index + 1].equalsIgnoreCase( "greenplum" ) )
			{
				vendor = EDbVendor.dbvgreenplum;
			}
			else if ( args[index + 1].equalsIgnoreCase( "redshift" ) )
			{
				vendor = EDbVendor.dbvredshift;
			}
		}

		boolean strict = argList.indexOf( "/s" ) != -1;

		boolean log = argList.indexOf( "/log" ) != -1;
		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;

		try
		{
			sw = new ByteArrayOutputStream( );
			pw = new PrintStream( sw );
			System.setErr( pw );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		Dlineage dlineage = new Dlineage( sqlFiles, vendor, strict, false );

		DlineageRelation relation = new DlineageRelation( );
		columnImpactResult impactResult = relation.generateColumnImpact( dlineage,
				null );

		List<ColumnMetaData[]> relations = relation.collectDlineageRelations( dlineage,
				impactResult );

		boolean forwardAnalyze = argList.indexOf( "/fo" ) != -1;
		if ( forwardAnalyze && argList.size( ) > argList.indexOf( "/fo" ) + 1 )
		{
			String tableColumn = argList.get( argList.indexOf( "/fo" ) + 1 );
			dlineage.forwardAnalyze( tableColumn, relations );
		}

		boolean backwardAnalyze = argList.indexOf( "/b" ) != -1;
		if ( backwardAnalyze && argList.size( ) > argList.indexOf( "/b" ) + 1 )
		{
			String viewColumn = argList.get( argList.indexOf( "/b" ) + 1 );
			dlineage.backwardAnalyze( viewColumn, relations );
		}

		boolean outputDDL = argList.indexOf( "/ddl" ) != -1;
		if ( outputDDL )
		{
			dlineage.outputDDLSchema( );
		}

		if ( !forwardAnalyze && !backwardAnalyze && !outputDDL )
		{
			dlineage.columnImpact( );
		}

		if ( pw != null )
		{
			pw.close( );
		}

		if ( sw != null )
		{
			String errorMessage = sw.toString( ).trim( );
			if ( errorMessage.length( ) > 0 )
			{
				if ( log )
				{
					try
					{
						pw = new PrintStream( new File( ".", "dlineage.log" ) );
						pw.print( errorMessage );
					}
					catch ( FileNotFoundException e )
					{
						e.printStackTrace( );
					}
				}

				System.setErr( systemSteam );
				System.err.println( errorMessage );
			}
		}
	}

	public Map<TableMetaData, List<ColumnMetaData>> getMetaData( )
	{
		return tableColumns;
	}

	public Pair<procedureImpactResult, List<ProcedureMetaData>> getProcedures( )
	{
		return procedures;
	}

	public boolean isStrict( )
	{
		return strict;
	}

	public EDbVendor getVendor( )
	{
		return vendor;
	}

}

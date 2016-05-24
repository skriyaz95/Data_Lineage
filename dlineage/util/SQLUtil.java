
package demos.dlineage.util;

import gudusoft.gsqlparser.TCustomSqlStatement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLUtil
{

	public static boolean isEmpty( String value )
	{
		return value == null || value.trim( ).length( ) == 0;
	}

	public static String getFileContent( File file )
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			InputStream is = new BufferedInputStream( new FileInputStream( file ) );
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			is.close( );
			out.close( );
			String content = new String( bytes );
			return content.trim( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public static String getInputStreamContent( InputStream is, boolean close )
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			if ( close )
			{
				is.close( );
			}
			out.close( );
			String content = new String( bytes );
			return content.trim( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public static String getFileContent( String filePath )
	{
		if ( filePath == null )
			return null;
		File file = new File( filePath );
		if ( !file.exists( ) || file.isDirectory( ) )
			return null;
		return getFileContent( file );
	}

	public static String trimObjectName( String string )
	{
		if ( string == null )
			return string;

		if ( string.indexOf( '.' ) != -1 )
		{
			String[] splits = string.split( "\\.", -1 );
			StringBuffer buffer = new StringBuffer( );
			for ( int i = 0; i < splits.length; i++ )
			{
				buffer.append( trimObjectName( splits[i] ) );
				if ( i < splits.length - 1 )
				{
					buffer.append( "." );
				}
			}
			string = buffer.toString( );
		}
		else
		{
			if ( string.startsWith( "\"" ) && string.endsWith( "\"" ) )
				return string.substring( 1, string.length( ) - 1 );

			if ( string.startsWith( "[" ) && string.endsWith( "]" ) )
				return string.substring( 1, string.length( ) - 1 );
		}
		return string;
	}

	public static void writeToFile( File file, InputStream source, boolean close )
	{
		BufferedInputStream bis = null;
		BufferedOutputStream fouts = null;
		try
		{
			bis = new BufferedInputStream( source );
			if ( !file.exists( ) )
			{
				if ( !file.getParentFile( ).exists( ) )
				{
					file.getParentFile( ).mkdirs( );
				}
				file.createNewFile( );
			}
			fouts = new BufferedOutputStream( new FileOutputStream( file ) );
			byte b[] = new byte[1024];
			int i = 0;
			while ( ( i = bis.read( b ) ) != -1 )
			{
				fouts.write( b, 0, i );
			}
			fouts.flush( );
			fouts.close( );
			if ( close )
				bis.close( );
		}
		catch ( IOException e )
		{
			Logger.getLogger( SQLUtil.class.getName( ) ).log( Level.WARNING,
					"Write file failed.", //$NON-NLS-1$
					e );
			try
			{
				if ( fouts != null )
					fouts.close( );
			}
			catch ( IOException f )
			{
				Logger.getLogger( SQLUtil.class.getName( ) )
						.log( Level.WARNING, "Close output stream failed.", f ); //$NON-NLS-1$
			}
			if ( close )
			{
				try
				{
					if ( bis != null )
						bis.close( );
				}
				catch ( IOException f )
				{
					Logger.getLogger( SQLUtil.class.getName( ) )
							.log( Level.WARNING, "Close input stream failed.", //$NON-NLS-1$
									f );
				}
			}
		}
	}

	public static void writeToFile( File file, String string )
			throws IOException
	{

		if ( !file.exists( ) )
		{
			if ( !file.getParentFile( ).exists( ) )
			{
				file.getParentFile( ).mkdirs( );
			}
			file.createNewFile( );
		}
		PrintWriter out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( file ) ) );
		if ( string != null )
			out.print( string );
		out.close( );
	}

	private static int virtualTableIndex = -1;
	private static Map<String, String> virtualTableNames = new HashMap<String, String>( );

	public synchronized static String generateVirtualTableName(
			TCustomSqlStatement stmt )
	{
		if ( virtualTableNames.containsKey( stmt.toString( ) ) )
			return virtualTableNames.get( stmt.toString( ) );
		else
		{
			String tableName = null;
			virtualTableIndex++;
			if ( virtualTableIndex == 0 )
			{
				tableName = "RESULT SET COLUMNS";
			}
			else
			{
				tableName = "RESULT SET COLUMNS " + virtualTableIndex;
			}
			virtualTableNames.put( stmt.toString( ), tableName );
			return tableName;
		}
	}

	public synchronized static void resetVirtualTableNames( )
	{
		virtualTableIndex = -1;
		virtualTableNames.clear( );
	}
}

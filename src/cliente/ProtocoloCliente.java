package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProtocoloCliente 
{
	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException, NoSuchAlgorithmException
	{
		String fromUser = "ack";
		
		String log = "";
		
		System.out.println("Cliente conectado al servidor con exito\n");
		
		pOut.println(fromUser);
		
		String fromServer = "";
		
		String id = pIn.readLine();
		log += "Client id: " + id + "\n";
		String cantConexiones = pIn.readLine();
		
		long timeI = System.currentTimeMillis();
		if((fromServer = pIn.readLine()) != null)
		{
			String archive = fromServer;
			pOut.println("ack");
			long timeF = System.currentTimeMillis();
			long totalTime = timeI - timeF;
			System.out.println("Recivido el archivo con exito\n");
			log += "File recieved succesfully \n";
			String fileName = pIn.readLine();
			String tamanoArchivo = pIn.readLine();
			
			System.out.println("File name: " + fileName);
			System.out.println("File length: " + tamanoArchivo);
			
			log += "File name: " + fileName + "\n";
			log += "File length: " + tamanoArchivo + "MB \n";
			log += "Total time: " + totalTime + " miliseconds \n";
			
			
			fromServer = pIn.readLine();
			if(fromServer != null)
			{
				System.out.println("Recivido el hash con exito\n");
				String password = archive;
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
				String sha256 = byte2str(digest);
				if(sha256.equals(fromServer))
				{
					System.out.println("El hash coincide con el enviado por el servidor\n");
					pOut.println("ack");
					createFile(archive, id, cantConexiones);
				}
				else
				{
					System.out.println("El hash no coincide con el enviado por el servidor");
				}
			}
		}
		
		createLog(log, id);
	}
	
	public static String byte2str( byte[] b )
	{
		// Encapsulamiento con hexadecimales
		String ret = "";
		for (int i = 0 ; i < b.length ; i++) {
			String g = Integer.toHexString(((char)b[i])&0x00ff);
			ret += (g.length()==1?"0":"") + g;
		}
		return ret;
	}
	
	/**
	 * 
	 * @param content content that is going to be in the log archive
	 * @throws IOException
	 */
	
	public static void createFile(String content, String id, String cantConexiones) throws IOException
	{   
		try {
            PrintWriter writer = new PrintWriter("home/servidor/aplicacion/ISIS3204/Data/ArchivosRecibidos/Cliente" + id + "-Prueba" + cantConexiones + ".txt", "UTF-8");
            writer.println(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 
	 * @param content content that is going to be in the log archive
	 * @throws IOException
	 */
	
	public static void createLog(String content, String idCliente) throws IOException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String h = dtf.format(LocalDateTime.now());
        String h2 = h.replace("/", "-");
        
		try {
            PrintWriter writer = new PrintWriter("home/servidor/aplicacion/ISIS3204/Data/logs/" + h2 + "-client" + idCliente + ".log", "UTF-8");
            writer.println(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}

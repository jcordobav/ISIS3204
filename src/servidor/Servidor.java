package servidor;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Servidor 
{
	private final static int PUERTO = 3400;
	private static int numeroThreads = 0;
	private static String nombreArchivo;
	private static String log;
	
	public static void main(String[] args) throws IOException
	{
		ServerSocket ss = null;
		boolean continuar = true;
		log = "";
		
		Path path = Paths.get("");
		String directoryName = path.toAbsolutePath().toString();
		System.out.println("Current Working Directory is = " +directoryName);
		
		Scanner leer = new Scanner(System.in);
		
		System.out.println("Servidor iniciado...");
		System.out.println("Ingrese la cantidad de clientes que desea atender en simultaneo: ");
		int cantClientes = leer.nextInt();
		CyclicBarrier bar = new CyclicBarrier(cantClientes);
		System.out.println("Ingrese el nombre del archivo que desea enviar: ");
		nombreArchivo = leer.next();
		
		
		
		try {
			ss = new ServerSocket(PUERTO);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int clientesAceptados = 0;
		while(continuar)
		{
			if(clientesAceptados % cantClientes == 0)
			{
				createLog(log);
			}
			Socket socket = ss.accept();
			ThreadServidor thread = new ThreadServidor(socket, numeroThreads, bar, nombreArchivo, cantClientes);
			numeroThreads ++;
			thread.start();
			clientesAceptados ++;
		}
		ss.close();
		leer.close();
	}
	
	public static synchronized void setLog(String file)
	{
		log += file;
	}
	
	public static synchronized int getNumThreads()
	{
		return numeroThreads;
	}
	
	public static synchronized void restNumThreads()
	{
		numeroThreads --;
	}
	
	public static String getNombreArchivo()
	{
		return nombreArchivo;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param content content that is going to be in the log archive
	 * @throws IOException
	 */
	
	public static void createLog(String content) throws IOException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String h = dtf.format(LocalDateTime.now());
        String h2 = h.replace("/", "-");
        
		try {
            PrintWriter writer = new PrintWriter("/home/servidor/aplicacion/ISIS3204/Data/logs/" + h2 + "-server.log", "UTF-8");
            writer.println(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}

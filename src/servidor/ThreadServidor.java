package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ThreadServidor extends Thread
{
	private Socket socket = null;
	private int numeroThreads;
	private CyclicBarrier bar;
	private String nombreArchivo;
	private int cantConexiones;
	
	public ThreadServidor(Socket socket, int numeroThreads, CyclicBarrier bar, String nombreArchivo, int cantConexiones)
	{
		this.socket = socket;
		this.numeroThreads = numeroThreads;
		this.bar = bar;
		this.nombreArchivo = nombreArchivo;
		this.cantConexiones = cantConexiones;
		
	}
	
	
	public void run()
	{
		PrintWriter escritor;
		BufferedReader lector;
		
		try {
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ProtocoloServidor.procesar(lector, escritor, bar, nombreArchivo, numeroThreads, cantConexiones);
			escritor.close();
			lector.close();
			socket.close();
			Servidor.restNumThreads();
		} catch (IOException | NoSuchAlgorithmException | InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}

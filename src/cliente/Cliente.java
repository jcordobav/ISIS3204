package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Cliente 
{
	public static final int PUERTO = 3400;
	public static final String SERVIDOR = "192.168.172.139";
	//public static final String SERVIDOR = "localhost";
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException
	{
		Socket socket = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		
		Path path = Paths.get("");
		String directoryName = path.toAbsolutePath().toString();
		System.out.println("Current Working Directory is = " +directoryName);
		
		System.out.println("Cliente... \n");		
		try {
			socket = new Socket(SERVIDOR, PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		ProtocoloCliente.procesar(stdIn,lector,escritor);
		
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
		
	}

}

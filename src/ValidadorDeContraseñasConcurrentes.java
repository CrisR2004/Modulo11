import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ValidadorDeContraseñasConcurrentes {
    private static final int LONGITUD_MINIMA_CONTRASEÑA = 8;
    private static final int MIN_LETRAS_MAYUSCULAS = 2;
    private static final int MIN_LETRAS_MINUSCULAS = 3;
    private static final int MIN_NUMEROS = 2;
    private static final Pattern PATRON_CONTRASEÑA = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]{3,})(?=.*[A-Z]{2,})(?=.*[~!@#$%^&*()_+=\\-{}|;':\"\\?><./,\\[\\]]).{" + LONGITUD_MINIMA_CONTRASEÑA + ",}$");
    private static final String REGISTRO_ARCHIVO = "registro.txt";

    public static void main(String[] args) {
        ExecutorService ejecutorDeTareas = Executors.newCachedThreadPool();
        Scanner lectorEntrada = new Scanner(System.in);

        try (BufferedWriter escritorRegistro = new BufferedWriter(new FileWriter(REGISTRO_ARCHIVO))) {
            System.out.println("Requisitos de contraseña:");
            System.out.println("- Longitud mínima: " + LONGITUD_MINIMA_CONTRASEÑA + " caracteres.");
            System.out.println("- Debe contener " + MIN_NUMEROS + " número, " + MIN_LETRAS_MAYUSCULAS + " letras mayúsculas, " + MIN_LETRAS_MINUSCULAS + " letras minúsculas y un caracter especial.\n");

            System.out.println("Ingrese la cantidad de contraseñas a validar:");
            int numeroDeContraseñas = lectorEntrada.nextInt();

            for (int i = 0; i < numeroDeContraseñas; i++) {
                System.out.println("Ingrese la contraseña " + (i + 1) + ":");
                String contraseña = lectorEntrada.next();

                Runnable tareaValidacionContraseña = () -> {
                    boolean esValida = validarContraseña(contraseña);
                    String resultado = "La contraseña '" + contraseña + "' es " + (esValida ? "válida" : "inválida") + "\n";
                    try {
                        escritorRegistro.write(resultado);
                    } catch (IOException e) {
                        System.err.println("Error al escribir en el archivo de registro: " + e.getMessage());
                    }
                };

                ejecutorDeTareas.submit(tareaValidacionContraseña);
            }
        } catch (IOException e) {
            System.err.println("Error al abrir o cerrar el archivo de registro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al leer la entrada: " + e.getMessage());
        } finally {
            ejecutorDeTareas.shutdown();
            lectorEntrada.close();
        }
    }

    private static boolean validarContraseña(String contraseña) {
        boolean esValida = true;

        if (contraseña.length() < LONGITUD_MINIMA_CONTRASEÑA) {
            System.out.println("- La longitud mínima requerida es de " + LONGITUD_MINIMA_CONTRASEÑA + " caracteres.");
            esValida = false;
        }
        if (!PATRON_CONTRASEÑA.matcher(contraseña).matches()) {
            System.out.println("- La contraseña no cumple con los requisitos necesarios.");
            esValida = false;
        }

        return esValida;
    }
}

package com.monica.casa.util;

/** Normalização de telefones para o formato exigido pela WAHA (somente dígitos, com DDI). */
public final class TelefoneUtil {

    private TelefoneUtil() {
    }

    /**
     * "(11) 99999-9999" -> "5511999999999". Números com 10 ou 11 dígitos recebem o DDI 55 (Brasil).
     */
    public static String normalizar(String telefone) {
        if (telefone == null) {
            return null;
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.startsWith("00")) {
            digitos = digitos.substring(2);
        }
        if (digitos.length() == 10 || digitos.length() == 11) {
            digitos = "55" + digitos;
        }
        return digitos;
    }

    /** Valida um telefone já normalizado (DDI + DDD + número: 12 a 15 dígitos). */
    public static boolean valido(String telefoneNormalizado) {
        return telefoneNormalizado != null && telefoneNormalizado.matches("\\d{12,15}");
    }

    /** Oculta o miolo do número para logs: 5511*****9999. */
    public static String mascarar(String telefone) {
        if (telefone == null || telefone.length() < 8) {
            return "***";
        }
        return telefone.substring(0, 4) + "*****" + telefone.substring(telefone.length() - 4);
    }
}

package sistemaaluguelcarros.bootstrap;

import java.util.Random;

final class SeedDataFactory {

    private SeedDataFactory() {
    }

    static String gerarCpfValidoFormatado(Random random) {
        int[] d = new int[11];
        for (int i = 0; i < 9; i++) {
            d[i] = random.nextInt(10);
        }
        if (todosIguais(d, 9)) {
            d[8] = (d[8] + 1) % 10;
        }

        d[9] = calcularDvCpf(d, 9, 10);
        d[10] = calcularDvCpf(d, 10, 11);
        return String.format(
                "%d%d%d.%d%d%d.%d%d%d-%d%d",
                d[0], d[1], d[2],
                d[3], d[4], d[5],
                d[6], d[7], d[8],
                d[9], d[10]
        );
    }

    static String gerarCnpjValidoSomenteDigitos(Random random) {
        int[] d = new int[14];
        // 12 primeiros dígitos (raiz + filial)
        for (int i = 0; i < 12; i++) {
            d[i] = random.nextInt(10);
        }
        // evita raiz toda igual (mais “realista”)
        if (todosIguais(d, 8)) {
            d[7] = (d[7] + 1) % 10;
        }

        d[12] = calcularDvCnpj(d, 12);
        d[13] = calcularDvCnpj(d, 13);

        StringBuilder sb = new StringBuilder(14);
        for (int i = 0; i < 14; i++) {
            sb.append(d[i]);
        }
        return sb.toString();
    }

    private static boolean todosIguais(int[] d, int len) {
        int primeiro = d[0];
        for (int i = 1; i < len; i++) {
            if (d[i] != primeiro) {
                return false;
            }
        }
        return true;
    }

    private static int calcularDvCpf(int[] d, int tamanhoBase, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanhoBase; i++) {
            soma += d[i] * (pesoInicial - i);
        }
        int resto = 11 - (soma % 11);
        return resto > 9 ? 0 : resto;
    }

    private static int calcularDvCnpj(int[] d, int tamanhoBase) {
        int[] pesos = tamanhoBase == 12
                ? new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < tamanhoBase; i++) {
            soma += d[i] * pesos[i];
        }
        int mod = soma % 11;
        return mod < 2 ? 0 : 11 - mod;
    }
}


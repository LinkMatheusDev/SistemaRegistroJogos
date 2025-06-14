import java.util.ArrayList;

public class Cadastro {
    private ArrayList<Produto> produtos = new ArrayList<>();

    public void incluir(Produto p) {
        produtos.add(p);
        System.out.println("Produto adicionado com sucesso.");
    }

    public Produto consultar(String nome) {
        for (Produto p : produtos) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
            }
        }
        return null;
    }

    public boolean alterar(String nome, double novoPreco) {
        Produto p = consultar(nome);
        if (p != null) {
            p.setPreco(novoPreco);
            return true;
        }
        return false;
    }

    public boolean excluir(String nome) {
        Produto p = consultar(nome);
        if (p != null) {
            produtos.remove(p);
            return true;
        }
        return false;
    }

    public void listar() {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (Produto p : produtos) {
            p.mostrarInfo();
            System.out.println("-----");
        }
    }
}

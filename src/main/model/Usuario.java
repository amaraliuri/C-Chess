// Usuario.java
package main.model;

public class Usuario {
    private int id;
    private String nome;
    private String login;
    private String senha;
    private Acessibilidade acessibilidade;

    public Usuario() {
        this.acessibilidade = new Acessibilidade();
    }

    public Usuario(int id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.acessibilidade = new Acessibilidade(id, false, 64);
    }

    public boolean autenticar(String login, String senha) {
        if (this.login == null || this.senha == null) {
            return false;
        }
        return this.login.equals(login) && this.senha.equals(senha);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        if (this.acessibilidade != null) {
            this.acessibilidade.setId(id);
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Acessibilidade getAcessibilidade() {
        return acessibilidade;
    }

    public void setAcessibilidade(Acessibilidade acessibilidade) {
        this.acessibilidade = acessibilidade;
    }
}
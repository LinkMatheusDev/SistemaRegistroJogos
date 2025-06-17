package com.sistemaregistrojogos.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Jogo extends Produto {
    private StringProperty genero;
    private StringProperty desenvolvedora;
    private StringProperty plataforma;
    private IntegerProperty anoLancamento;
    private DoubleProperty classificacao;
    private StringProperty descricao;

    public Jogo() {
        super();
        this.genero = new SimpleStringProperty();
        this.desenvolvedora = new SimpleStringProperty();
        this.plataforma = new SimpleStringProperty();
        this.anoLancamento = new SimpleIntegerProperty();
        this.classificacao = new SimpleDoubleProperty();
        this.descricao = new SimpleStringProperty();
    }

    public Jogo(String nome, double preco, String genero, String desenvolvedora) {
        super(nome, preco);
        this.genero = new SimpleStringProperty(genero);
        this.desenvolvedora = new SimpleStringProperty(desenvolvedora);
        this.plataforma = new SimpleStringProperty();
        this.anoLancamento = new SimpleIntegerProperty();
        this.classificacao = new SimpleDoubleProperty();
        this.descricao = new SimpleStringProperty();
    }

    public Jogo(int id, String nome, double preco, String genero, String desenvolvedora, 
               String plataforma, int anoLancamento, double classificacao, String descricao,
               LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        super(id, nome, preco, dataCadastro, dataAtualizacao);
        this.genero = new SimpleStringProperty(genero);
        this.desenvolvedora = new SimpleStringProperty(desenvolvedora);
        this.plataforma = new SimpleStringProperty(plataforma);
        this.anoLancamento = new SimpleIntegerProperty(anoLancamento);
        this.classificacao = new SimpleDoubleProperty(classificacao);
        this.descricao = new SimpleStringProperty(descricao);
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Gênero: " + getGenero());
        System.out.println("Desenvolvedora: " + getDesenvolvedora());
        System.out.println("Plataforma: " + getPlataforma());
        System.out.println("Ano de Lançamento: " + getAnoLancamento());
        System.out.println("Classificação: " + String.format("%.1f", getClassificacao()));
        System.out.println("Descrição: " + getDescricao());
    }

    // Getters e Setters para Gênero
    public String getGenero() {
        return genero.get();
    }

    public StringProperty generoProperty() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero.set(genero);
        setDataAtualizacao(LocalDateTime.now());
    }

    // Getters e Setters para Desenvolvedora
    public String getDesenvolvedora() {
        return desenvolvedora.get();
    }

    public StringProperty desenvolvedoraProperty() {
        return desenvolvedora;
    }

    public void setDesenvolvedora(String desenvolvedora) {
        this.desenvolvedora.set(desenvolvedora);
        setDataAtualizacao(LocalDateTime.now());
    }

    // Getters e Setters para Plataforma
    public String getPlataforma() {
        return plataforma.get();
    }

    public StringProperty plataformaProperty() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma.set(plataforma);
        setDataAtualizacao(LocalDateTime.now());
    }

    // Getters e Setters para Ano de Lançamento
    public int getAnoLancamento() {
        return anoLancamento.get();
    }

    public IntegerProperty anoLancamentoProperty() {
        return anoLancamento;
    }

    public void setAnoLancamento(int anoLancamento) {
        this.anoLancamento.set(anoLancamento);
        setDataAtualizacao(LocalDateTime.now());
    }

    // Getters e Setters para Classificação
    public double getClassificacao() {
        return classificacao.get();
    }

    public DoubleProperty classificacaoProperty() {
        return classificacao;
    }

    public void setClassificacao(double classificacao) {
        this.classificacao.set(classificacao);
        setDataAtualizacao(LocalDateTime.now());
    }

    // Getters e Setters para Descrição
    public String getDescricao() {
        return descricao.get();
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao.set(descricao);
        setDataAtualizacao(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Jogo{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", preco=" + getPreco() +
                ", genero='" + getGenero() + '\'' +
                ", desenvolvedora='" + getDesenvolvedora() + '\'' +
                ", plataforma='" + getPlataforma() + '\'' +
                ", anoLancamento=" + getAnoLancamento() +
                ", classificacao=" + getClassificacao() +
                ", descricao='" + getDescricao() + '\'' +
                ", dataCadastro=" + getDataCadastro() +
                ", dataAtualizacao=" + getDataAtualizacao() +
                '}';
    }
} 
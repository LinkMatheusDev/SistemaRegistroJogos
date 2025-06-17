package com.sistemaregistrojogos.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Produto {
    private IntegerProperty id;
    private StringProperty nome;
    private DoubleProperty preco;
    private ObjectProperty<LocalDateTime> dataCadastro;
    private ObjectProperty<LocalDateTime> dataAtualizacao;

    public Produto() {
        this.id = new SimpleIntegerProperty();
        this.nome = new SimpleStringProperty();
        this.preco = new SimpleDoubleProperty();
        this.dataCadastro = new SimpleObjectProperty<>();
        this.dataAtualizacao = new SimpleObjectProperty<>();
    }

    public Produto(String nome, double preco) {
        this();
        setNome(nome);
        setPreco(preco);
        setDataCadastro(LocalDateTime.now());
        setDataAtualizacao(LocalDateTime.now());
    }

    public Produto(int id, String nome, double preco, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        this();
        setId(id);
        setNome(nome);
        setPreco(preco);
        setDataCadastro(dataCadastro);
        setDataAtualizacao(dataAtualizacao);
    }

    // Métodos para exibir informações
    public void mostrarInfo() {
        System.out.println("ID: " + getId());
        System.out.println("Nome: " + getNome());
        System.out.println("Preço: R$ " + String.format("%.2f", getPreco()));
        System.out.println("Data de cadastro: " + getDataCadastro());
        System.out.println("Última atualização: " + getDataAtualizacao());
    }

    // Getters e Setters para ID
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // Getters e Setters para Nome
    public String getNome() {
        return nome.get();
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    // Getters e Setters para Preço
    public double getPreco() {
        return preco.get();
    }

    public DoubleProperty precoProperty() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco.set(preco);
        setDataAtualizacao(LocalDateTime.now());
    }

    // Getters e Setters para Data de Cadastro
    public LocalDateTime getDataCadastro() {
        return dataCadastro.get();
    }

    public ObjectProperty<LocalDateTime> dataCadastroProperty() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro.set(dataCadastro);
    }

    // Getters e Setters para Data de Atualização
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao.get();
    }

    public ObjectProperty<LocalDateTime> dataAtualizacaoProperty() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao.set(dataAtualizacao);
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", preco=" + getPreco() +
                ", dataCadastro=" + getDataCadastro() +
                ", dataAtualizacao=" + getDataAtualizacao() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produto produto = (Produto) obj;
        return getId() == produto.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
} 
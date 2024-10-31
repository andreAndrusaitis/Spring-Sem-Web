package br.com.andrusaitis.screammatch.service;

public interface IConverterDados {
    <T> T obterDados(String json, Class <T> classe);
}

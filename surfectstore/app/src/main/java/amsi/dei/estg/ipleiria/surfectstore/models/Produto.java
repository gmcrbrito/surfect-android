package amsi.dei.estg.ipleiria.surfectstore.models;

public class Produto {
    private int idProduto;
    private String nomeProduto;
    private int precoProduto;
    private int stockProduto;
    private String descricaoProduto;
    private int descontoProduto;
    private int categoriaProduto;
    private String fotoProduto;
    private int quantidadeProduto;

    public Produto() {
    }

    public Produto(String nomeProduto, int precoProduto, int stockProduto, String descricaoProduto, int descontoProduto, int categoriaProduto, String fotoProduto, int quantidadeProduto, int idProduto) {
        this.nomeProduto = nomeProduto;
        this.precoProduto = precoProduto;
        this.stockProduto = stockProduto;
        this.descricaoProduto = descricaoProduto;
        this.descontoProduto = descontoProduto;
        this.categoriaProduto = categoriaProduto;
        this.fotoProduto = fotoProduto;
        this.quantidadeProduto = quantidadeProduto;
        this.idProduto = idProduto;
    }

    // Getters
    public int getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }
    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getPrecoProduto() {
        return precoProduto;
    }

    public int getStockProduto() {
        return stockProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public int getDescontoProduto() {
        return descontoProduto;
    }

    public int getCategoriaProduto() {
        return categoriaProduto;
    }

    public String getFotoProduto() {
        return fotoProduto;
    }

    public int getQuantidadeProduto() {
        return quantidadeProduto;
    }

    // Setters

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public void setPrecoProduto(int precoProduto) {
        this.precoProduto = precoProduto;
    }

    public void setStockProduto(int stockProduto) {
        this.stockProduto = stockProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public void setDescontoProduto(int descontoProduto) {
        this.descontoProduto = descontoProduto;
    }

    public void setCategoriaProduto(int categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public void setFotoProduto(String fotoProduto) {
        this.fotoProduto = fotoProduto;
    }

    public void setQuantidadeProduto(int quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }
}

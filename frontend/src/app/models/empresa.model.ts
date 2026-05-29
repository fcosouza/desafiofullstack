export interface Empresa {
  id?: number;
  cnpj: string;
  nomeFantasia: string;
  cep: string;
  estado: string;
  cidade: string;
  fornecedorIds?: number[];
}

export interface EmpresaResumo {
  id: number;
  cnpj: string;
  nomeFantasia: string;
  cep: string;
  estado: string;
  cidade: string;
  totalFornecedores: number;
}

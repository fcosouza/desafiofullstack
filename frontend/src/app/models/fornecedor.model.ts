export type TipoPessoa = 'FISICA' | 'JURIDICA';

export interface Fornecedor {
  id?: number;
  tipoPessoa: TipoPessoa;
  nome: string;
  cpfCnpj: string;
  email: string;
  cep: string;
  rg?: string;
  dataNascimento?: string;
  empresaIds?: number[];
}

export interface FornecedorResumo {
  id: number;
  tipoPessoa: TipoPessoa;
  nome: string;
  cpfCnpj: string;
  email: string;
  cep: string;
  rg?: string;
  dataNascimento?: string;
  totalEmpresas: number;
}

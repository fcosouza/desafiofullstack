import { Routes } from '@angular/router';
import { EmpresaListComponent } from './pages/empresa-list/empresa-list.component';
import { EmpresaFormComponent } from './pages/empresa-form/empresa-form.component';
import { FornecedorListComponent } from './pages/fornecedor-list/fornecedor-list.component';
import { FornecedorFormComponent } from './pages/fornecedor-form/fornecedor-form.component';

export const routes: Routes = [
  { path: '', redirectTo: 'empresas', pathMatch: 'full' },
  { path: 'empresas', component: EmpresaListComponent },
  { path: 'empresas/novo', component: EmpresaFormComponent },
  { path: 'empresas/editar/:id', component: EmpresaFormComponent },
  { path: 'fornecedores', component: FornecedorListComponent },
  { path: 'fornecedores/novo', component: FornecedorFormComponent },
  { path: 'fornecedores/editar/:id', component: FornecedorFormComponent },
];

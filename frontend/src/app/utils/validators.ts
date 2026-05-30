import { AbstractControl, ValidationErrors } from '@angular/forms';

export function dataNaoFutura(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;
  const data = new Date(control.value + 'T00:00:00');
  return data > new Date() ? { dataFutura: true } : null;
}

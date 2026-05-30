export function maskCpf(value: string): string {
  let v = value.replace(/\D/g, '');
  if (v.length > 11) v = v.substring(0, 11);
  if (v.length > 9) v = v.replace(/^(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
  else if (v.length > 6) v = v.replace(/^(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
  else if (v.length > 3) v = v.replace(/^(\d{3})(\d{1,3})/, '$1.$2');
  return v;
}

export function maskCnpj(value: string): string {
  let v = value.replace(/\D/g, '');
  if (v.length > 14) v = v.substring(0, 14);
  if (v.length > 12) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{1,2})/, '$1.$2.$3/$4-$5');
  else if (v.length > 8) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{1,4})/, '$1.$2.$3/$4');
  else if (v.length > 5) v = v.replace(/^(\d{2})(\d{3})(\d{1,3})/, '$1.$2.$3');
  else if (v.length > 2) v = v.replace(/^(\d{2})(\d{1,3})/, '$1.$2');
  return v;
}

export function maskCep(value: string): string {
  let v = value.replace(/\D/g, '');
  if (v.length > 8) v = v.substring(0, 8);
  if (v.length > 5) v = v.replace(/^(\d{5})(\d{1,3})/, '$1-$2');
  return v;
}

export function maskRg(value: string): string {
  let v = value.replace(/\D/g, '');
  if (v.length > 9) v = v.substring(0, 9);
  if (v.length > 8) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{1})/, '$1.$2.$3-$4');
  else if (v.length > 5) v = v.replace(/^(\d{2})(\d{3})(\d{1,3})/, '$1.$2.$3');
  else if (v.length > 2) v = v.replace(/^(\d{2})(\d{1,3})/, '$1.$2');
  return v;
}

export function applyMask(event: Event, maskFn: (value: string) => string): string {
  const input = event.target as HTMLInputElement;
  return maskFn(input.value);
}

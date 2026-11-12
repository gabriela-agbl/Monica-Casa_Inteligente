import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, KeyRound, Mail, Phone, UserRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { errorMessage } from '../services/api';
import { Button, ErrorBox, Field } from '../components/UI';
import AuthShell, { AuthHeader, SocialRow } from './AuthShell';

const PHONE_REGEX = /^[0-9()+\-\s]{10,20}$/;

export default function RegisterPage() {
  const { register, user } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ nome: '', email: '', telefone: '', senha: '', confirmar: '' });
  const [show, setShow] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  if (user) return <Navigate to="/" replace />;

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    if (!PHONE_REGEX.test(form.telefone)) return setError('Telefone inválido. Use DDD + número, ex.: (11) 99999-9999.');
    if (form.senha.length < 6) return setError('A senha deve ter no mínimo 6 caracteres.');
    if (form.senha !== form.confirmar) return setError('As senhas não conferem.');

    setBusy(true);
    try {
      await register({ nome: form.nome.trim(), email: form.email.trim().toLowerCase(), telefone: form.telefone, senha: form.senha });
      navigate('/login', { state: { registered: true } });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  const eye = (
    <button type="button" className="field__eye" onClick={() => setShow(!show)} aria-label={show ? 'Ocultar senha' : 'Mostrar senha'}>
      {show ? <EyeOff size={16} /> : <Eye size={16} />}
    </button>
  );

  return (
    <AuthShell imageSide="left" bubble="Você vai precisar de uma conta!">
      <AuthHeader title="Crie sua conta" />
      <form onSubmit={submit} className="auth-form">
        <Field icon={UserRound} name="nome" placeholder="Nome completo" aria-label="Nome completo" autoComplete="name" value={form.nome} onChange={change} required maxLength={100} />
        <Field icon={Mail} name="email" type="email" placeholder="E-mail" aria-label="E-mail" autoComplete="email" value={form.email} onChange={change} required />
        <Field icon={Phone} name="telefone" type="tel" placeholder="Telefone com DDD" aria-label="Telefone com DDD" autoComplete="tel" value={form.telefone} onChange={change} required />
        <div className="auth-form__pair">
          <Field icon={KeyRound} name="senha" type={show ? 'text' : 'password'} placeholder="Senha" aria-label="Senha" autoComplete="new-password" value={form.senha} onChange={change} required right={eye} />
          <Field name="confirmar" type={show ? 'text' : 'password'} placeholder="Confirmar senha" aria-label="Confirmar senha" autoComplete="new-password" value={form.confirmar} onChange={change} required />
        </div>
        <ErrorBox>{error}</ErrorBox>
        <Button type="submit" className="btn--auth" loading={busy}>Criar conta</Button>
      </form>

      <SocialRow />
      <p className="auth-switch">Já tem uma conta? <Link to="/login">Entrar</Link></p>
    </AuthShell>
  );
}

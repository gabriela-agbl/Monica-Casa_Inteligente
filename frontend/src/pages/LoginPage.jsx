import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, KeyRound, Mail } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { errorMessage } from '../services/api';
import { Button, ErrorBox, Field } from '../components/UI';
import AuthShell, { AuthHeader, SocialRow } from './AuthShell';

export default function LoginPage() {
  const { login, user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const toast = useToast();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [show, setShow] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  if (user) return <Navigate to="/" replace />;

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      await login(email, password);
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.response?.status === 401 ? 'E-mail ou senha inválidos.' : errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthShell imageSide="right" bubble="Estava te esperando...">
      <AuthHeader title="Bem-vindo de volta!" />
      {location.state?.registered && <div className="success-box">Conta criada com sucesso! Entre para continuar.</div>}

      <form onSubmit={submit} className="auth-form" noValidate={false}>
        <Field icon={Mail} type="email" placeholder="E-mail" aria-label="E-mail" autoComplete="username" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <Field
          icon={KeyRound}
          type={show ? 'text' : 'password'}
          placeholder="Senha"
          aria-label="Senha"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          right={(
            <button type="button" className="field__eye" onClick={() => setShow(!show)} aria-label={show ? 'Ocultar senha' : 'Mostrar senha'}>
              {show ? <EyeOff size={17} /> : <Eye size={17} />}
            </button>
          )}
        />
        <button type="button" className="forgot-link" onClick={() => toast('Peça ao morador (administrador) da casa para redefinir sua senha.', 'error')}>
          Esqueceu a senha?
        </button>
        <ErrorBox>{error}</ErrorBox>
        <Button type="submit" className="btn--auth" loading={busy}>Entrar</Button>
      </form>

      <SocialRow />
      <p className="auth-switch">Não tem uma conta? <Link to="/cadastro">Cadastre-se</Link></p>
    </AuthShell>
  );
}

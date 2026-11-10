import { LoaderCircle, X } from 'lucide-react';
export function Button({ children, variant='primary', className='', loading=false, ...props }) { return <button className={`btn btn-${variant} ${className}`} disabled={loading || props.disabled} {...props}>{loading && <LoaderCircle size={16} className="spin"/>}{children}</button>; }
export function Card({ children, className='' }) { return <section className={`card ${className}`}>{children}</section>; }
export function Field({ label, ...props }) { return <label className="field"><span>{label}</span><input {...props}/></label>; }
export function SelectField({ label, children, ...props }) { return <label className="field"><span>{label}</span><select {...props}>{children}</select></label>; }
export function Modal({ title, onClose, children, wide=false }) { return <div className="modal-backdrop" onMouseDown={(e)=>e.target===e.currentTarget&&onClose()}><div className={`modal ${wide?'modal-wide':''}`}><div className="modal-head"><h2>{title}</h2><button className="icon-button" onClick={onClose} aria-label="Fechar"><X size={19}/></button></div>{children}</div></div>; }
export function Empty({ title='Nada por aqui ainda', text='Quando houver informações, elas aparecerão nesta área.' }) { return <div className="empty"><span>✦</span><strong>{title}</strong><p>{text}</p></div>; }
export function Status({ active, children }) { return <span className={`status ${active?'status-on':'status-off'}`}><i/>{children || (active?'Ligado':'Desligado')}</span>; }
export function PageTitle({ eyebrow='CENTRAL DA CASA', title, subtitle, action }) { return <div className="page-title"><div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1>{subtitle&&<p>{subtitle}</p>}</div>{action}</div>; }
export function Loading() { return <div className="loading"><LoaderCircle className="spin"/><span>Carregando...</span></div>; }

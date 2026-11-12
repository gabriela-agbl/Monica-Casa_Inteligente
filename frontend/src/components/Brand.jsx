/** Logo: casa verde + "Mônica". */
export function HouseMark({ size = 44 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 48 48" aria-hidden="true" className="house-mark">
      <path d="M33 9h5v9l-5-4.5V9Z" fill="#17a342" />
      <path d="M24 5 4 22.5h6.5V41h27V22.5H44L24 5Z" fill="#1fc24f" />
      <path d="M24 5 4 22.5h6.5L24 10.7l13.5 11.8H44L24 5Z" fill="#35d96a" />
      <path d="M19.5 41V31.5a4.5 4.5 0 0 1 9 0V41Z" fill="#0b0f14" />
    </svg>
  );
}

export default function Brand({ size = 44, className = '' }) {
  return (
    <div className={`brand ${className}`}>
      <HouseMark size={size} />
      <span>Mônica</span>
    </div>
  );
}

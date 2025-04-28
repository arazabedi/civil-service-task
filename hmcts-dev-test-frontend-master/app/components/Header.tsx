import { ThemeToggle } from "./ThemeToggle";

export const Header: React.FC = () => {
  return (
<header className="relative bg-[#0b0c0c] text-white w-full">
  <div className="container mx-auto flex items-center justify-between py-3 px-4">
    <a href="https://www.gov.uk" className="flex items-center">
      <span className="text-2xl font-bold tracking-tight">HMCTS Dev Test</span>
      <p className="text-end ml-3">Task API</p>
    </a>
    <nav className="flex space-x-4">
      <a href="#" className="text-white hover:underline">Home</a>
      <a href="#" className="text-white hover:underline">Services</a>
      <a href="#" className="text-white hover:underline">Contact</a>
    </nav>
    <ThemeToggle />
  </div>
  <div className="absolute bottom-0 left-0 right-0 h-[10px] bg-[#1F70B8] pointer-events-none" />
</header>

  );
};

import React from "react";

const sections = [
  { title: "Product", links: ["Transfers", "Players", "Clubs", "Competitions"] },
  { title: "Resources", links: ["Statistics", "Market Values", "News", "API"] },
  { title: "Company", links: ["About Us", "Careers", "Contact", "Press"] },
  { title: "Legal", links: ["Privacy Policy", "Terms of Service", "Cookie Policy"] },
];

const SocialIcon = ({ children, label }) => (
  <a
    href="#"
    aria-label={label}
    className="h-11 w-11 rounded-xl bg-[#1A1A1A] text-[#B3B3B3] hover:text-[#FFFFFF] hover:bg-[#FF6B00] grid place-items-center transition-colors"
  >
    {children}
  </a>
);

const Footer = () => {
  return (
    <footer className="mt-auto bg-[#0F0F0F] text-[#B3B3B3] border-t border-[#1A1A1A]">
      <div className="max-w-[1600px] mx-auto px-4 sm:px-8 lg:px-12 py-10 sm:py-12">
        <div className="grid gap-10 lg:grid-cols-[1.1fr_repeat(4,minmax(0,1fr))]">
          <div>
            <div className="flex items-center gap-3 mb-5">
              <div className="h-12 w-12 rounded-xl bg-[#FF6B00] text-[#FFFFFF] font-extrabold text-3xl leading-none grid place-items-center">
                FI
              </div>
              <h2 className="text-3xl sm:text-4xl font-bold leading-none tracking-tight text-[#FFFFFF]">
                <span>Football</span>
                <span className="text-[#FF6B00]">Insider</span>
              </h2>
            </div>
            <p className="max-w-sm text-sm sm:text-base leading-snug text-[#B3B3B3]">
              Your ultimate destination for football transfers, player valuations, and club statistics.
            </p>
            <div className="mt-6 flex gap-3">
              <SocialIcon label="Twitter">
                <svg viewBox="0 0 24 24" className="h-5 w-5 fill-current" aria-hidden="true">
                  <path d="M18.9 2H22l-6.77 7.74L23 22h-6.18l-4.84-6.94L5.9 22H2.8l7.24-8.28L1 2h6.33l4.38 6.31L18.9 2zM17.8 20h1.71L6.39 3.9H4.56L17.8 20z" />
                </svg>
              </SocialIcon>
              <SocialIcon label="Instagram">
                <svg viewBox="0 0 24 24" className="h-5 w-5 fill-current" aria-hidden="true">
                  <path d="M16 2H8a6 6 0 0 0-6 6v8a6 6 0 0 0 6 6h8a6 6 0 0 0 6-6V8a6 6 0 0 0-6-6zm4 14a4 4 0 0 1-4 4H8a4 4 0 0 1-4-4V8a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v8z" />
                  <path d="M12 7a5 5 0 1 0 5 5 5 5 0 0 0-5-5zm0 8a3 3 0 1 1 3-3 3 3 0 0 1-3 3zM17.5 6.5a1.2 1.2 0 1 0 1.2 1.2 1.2 1.2 0 0 0-1.2-1.2z" />
                </svg>
              </SocialIcon>
              <SocialIcon label="YouTube">
                <svg viewBox="0 0 24 24" className="h-5 w-5 fill-current" aria-hidden="true">
                  <path d="M21.6 7.2a2.9 2.9 0 0 0-2-2C17.8 4.7 12 4.7 12 4.7s-5.8 0-7.6.5a2.9 2.9 0 0 0-2 2A30.4 30.4 0 0 0 2 12a30.4 30.4 0 0 0 .4 4.8 2.9 2.9 0 0 0 2 2c1.8.5 7.6.5 7.6.5s5.8 0 7.6-.5a2.9 2.9 0 0 0 2-2A30.4 30.4 0 0 0 22 12a30.4 30.4 0 0 0-.4-4.8zM10 15.5v-7l6 3.5-6 3.5z" />
                </svg>
              </SocialIcon>
              <SocialIcon label="GitHub">
                <svg viewBox="0 0 24 24" className="h-5 w-5 fill-current" aria-hidden="true">
                  <path d="M12 .5a11.5 11.5 0 0 0-3.64 22.42c.58.1.79-.25.79-.56v-2.17c-3.2.7-3.87-1.35-3.87-1.35a3 3 0 0 0-1.25-1.66c-1-.68.08-.67.08-.67a2.37 2.37 0 0 1 1.73 1.16 2.4 2.4 0 0 0 3.29.93 2.38 2.38 0 0 1 .72-1.5c-2.55-.29-5.24-1.28-5.24-5.7a4.46 4.46 0 0 1 1.18-3.09 4.14 4.14 0 0 1 .11-3.05s1-.31 3.17 1.18a11 11 0 0 1 5.77 0c2.2-1.49 3.17-1.18 3.17-1.18a4.14 4.14 0 0 1 .11 3.05 4.46 4.46 0 0 1 1.18 3.09c0 4.43-2.7 5.4-5.27 5.68a2.67 2.67 0 0 1 .76 2.08v3.09c0 .31.21.67.8.56A11.5 11.5 0 0 0 12 .5z" />
                </svg>
              </SocialIcon>
            </div>
          </div>

          {sections.map((section) => (
            <div key={section.title}>
              <h3 className="text-2xl sm:text-3xl font-semibold text-[#FFFFFF] mb-5">{section.title}</h3>
              <ul className="space-y-4 text-sm sm:text-base text-[#B3B3B3]">
                {section.links.map((link) => (
                  <li key={link}>
                    <a href="#" className="hover:text-[#FF8533] transition-colors">
                      {link}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="mt-10 border-t border-[#1A1A1A] pt-8 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 text-sm sm:text-base text-[#B3B3B3]">
          <p>&copy; 2025 FootballInsider. All rights reserved.</p>
          <p className="flex items-center gap-2">
            <span>Made with</span>
            <span className="text-[#FF6B00]">{"\u25C9"}</span>
            <span>for football fans worldwide</span>
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

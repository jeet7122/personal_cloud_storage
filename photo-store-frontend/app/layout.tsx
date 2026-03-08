import type { Metadata } from "next";
import {
    ClerkProvider,
    Show,
    SignInButton,
    SignUpButton,
    UserButton,
} from "@clerk/nextjs";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

export const metadata: Metadata = {
    title: "Personal Cloud",
    description: "Private photo storage app",
};

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <ClerkProvider>
            <html lang="en">
            <body className={`${geistSans.variable} ${geistMono.variable} antialiased bg-[#f8f9fa]`}>
            <header className="border-b border-gray-200 bg-white">
                <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
                    <div>
                        <h1 className="text-lg font-semibold text-gray-900">Personal Cloud</h1>
                        <p className="text-sm text-gray-500">Your private photo storage</p>
                    </div>

                    <div className="flex items-center gap-3">
                        <Show when="signed-out">
                            <SignInButton mode="modal">
                                <button className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100">
                                    Sign in
                                </button>
                            </SignInButton>

                            <SignUpButton mode="modal">
                                <button className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700">
                                    Sign up
                                </button>
                            </SignUpButton>
                        </Show>

                        <Show when="signed-in">
                            <UserButton />
                        </Show>
                    </div>
                </div>
            </header>

            {children}
            </body>
            </html>
        </ClerkProvider>
    );
}
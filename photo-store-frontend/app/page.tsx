import { auth, currentUser } from "@clerk/nextjs/server";
import PhotoDashboard from "@/components/photo-dashboard";

const ALLOWED_EMAIL = process.env.ALLOWED_USER_EMAIL!;

export default async function HomePage() {
    await auth.protect();

    const user = await currentUser();

    const email = user?.emailAddresses?.find(
        (e) => e.id === user.primaryEmailAddressId
    )?.emailAddress;

    if (email !== ALLOWED_EMAIL) {
        return <div className="p-8">You are not allowed to use this app.</div>;
    }

    return <PhotoDashboard />;
}
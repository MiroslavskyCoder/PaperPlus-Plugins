import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { Checkbox } from "@/components/ui/checkbox"
import { Label } from "@/components/ui/label"

interface SettingsTabProps {
    
}

export function SettingsTab({ }: SettingsTabProps) {
    // const { data: authPlayer } = useAuthPlayer();
    return (
        <div className="space-y-6 animate-fade-in w-full h-full">
            <div className="flex flex-col gap-6">
                <div className="flex items-center gap-3">
                    <Checkbox id="authplayer-checkbox" />
                    <Label htmlFor="authplayer-checkbox">Auth Player</Label>
                </div>
                {}
            </div> 
        </div>
    );
}
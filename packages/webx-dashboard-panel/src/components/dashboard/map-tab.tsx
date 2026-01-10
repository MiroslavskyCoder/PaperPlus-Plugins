import { useEffect, useRef, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';

interface Entity {
  type: string;
  name: string;
  x: number;
  y: number;
  z: number;
}

interface MapTabProps {
  entities: Entity[];
  mapImage: string;
}

export function MapTab({ entities, mapImage }: MapTabProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [centerX, setCenterX] = useState(0);
  const [centerZ, setCenterZ] = useState(0);
  const [scale, setScale] = useState(0.1);
  const [isDragging, setIsDragging] = useState(false);
  const [lastMouseX, setLastMouseX] = useState(0);
  const [lastMouseZ, setLastMouseZ] = useState(0);

  useEffect(() => {
    const player = entities.find((e) => e.type === 'player');
    if (player) {
      setCenterX(player.x);
      setCenterZ(player.z);
    }
  }, [entities]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (mapImage) {
      const img = new Image();
      img.onload = () => ctx.drawImage(img, 0, 0);
      img.src = 'data:image/png;base64,' + mapImage;
    } else {
      // Draw grid
      ctx.strokeStyle = '#333';
      ctx.lineWidth = 1;
      const gridSize = 16 * scale; // 16 blocks per chunk
      for (let x = 0; x < canvas.width; x += gridSize) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, canvas.height);
        ctx.stroke();
      }
      for (let z = 0; z < canvas.height; z += gridSize) {
        ctx.beginPath();
        ctx.moveTo(0, z);
        ctx.lineTo(canvas.width, z);
        ctx.stroke();
      }
      // Draw entities
      entities.forEach(entity => {
        const x = (entity.x - centerX) * scale + canvas.width / 2;
        const z = (entity.z - centerZ) * scale + canvas.height / 2;
        if (x >= 0 && x <= canvas.width && z >= 0 && z <= canvas.height) {
          ctx.fillStyle = entity.type === 'player' ? 'blue' : 'red';
          ctx.fillRect(x - 2, z - 2, 4, 4);
          ctx.fillStyle = 'white';
          ctx.font = '10px Arial';
          ctx.fillText(entity.name, x + 5, z);
        }
      });
    }
  }, [entities, centerX, centerZ, scale, mapImage]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const handleMouseDown = (e: MouseEvent) => {
      setIsDragging(true);
      setLastMouseX(e.clientX);
      setLastMouseZ(e.clientY);
    };

    const handleMouseMove = (e: MouseEvent) => {
      if (isDragging) {
        const dx = e.clientX - lastMouseX;
        const dz = e.clientY - lastMouseZ;
        setCenterX(centerX - dx / scale);
        setCenterZ(centerZ - dz / scale);
        setLastMouseX(e.clientX);
        setLastMouseZ(e.clientY);
      }
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    const handleWheel = (e: WheelEvent) => {
      e.preventDefault();
      const zoomFactor = e.deltaY > 0 ? 0.9 : 1.1;
      setScale(scale * zoomFactor);
    };

    canvas.addEventListener('mousedown', handleMouseDown);
    canvas.addEventListener('mousemove', handleMouseMove);
    canvas.addEventListener('mouseup', handleMouseUp);
    canvas.addEventListener('wheel', handleWheel);

    return () => {
      canvas.removeEventListener('mousedown', handleMouseDown);
      canvas.removeEventListener('mousemove', handleMouseMove);
      canvas.removeEventListener('mouseup', handleMouseUp);
      canvas.removeEventListener('wheel', handleWheel);
    };
  }, [isDragging, lastMouseX, lastMouseZ, centerX, centerZ, scale]);

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="grid gap-6 lg:grid-cols-4">
        <Card className="lg:col-span-3 bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
              </svg>
              World Map
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="relative group">
              <canvas
                ref={canvasRef}
                width={800}
                height={600}
                className="border-none rounded-lg bg-gradient-to-br from-muted/20 to-muted/40 cursor-move transition-all duration-300 group-hover:shadow-lg"
                style={{ maxWidth: '100%', height: 'auto' }}
              />
              <div className="absolute top-4 left-4 bg-background/90 backdrop-blur-sm rounded-lg p-3 border border-border/50 shadow-lg">
                <div className="text-sm space-y-1 font-mono">
                  <p><span className="text-muted-foreground">Center:</span> ({centerX.toFixed(1)}, {centerZ.toFixed(1)})</p>
                  <p><span className="text-muted-foreground">Scale:</span> {scale.toFixed(2)}x</p>
                  <p><span className="text-muted-foreground">Entities:</span> {entities.length}</p>
                </div>
              </div>
              <div className="absolute top-4 right-4 bg-background/90 backdrop-blur-sm rounded-lg p-2 border border-border/50 shadow-lg">
                <div className="flex flex-col gap-2">
                  <button
                    onClick={() => setScale(scale * 1.2)}
                    className="w-8 h-8 bg-primary hover:bg-primary/90 text-primary-foreground rounded flex items-center justify-center text-sm font-bold transition-all duration-200 hover:scale-105"
                  >
                    +
                  </button>
                  <button
                    onClick={() => setScale(scale * 0.8)}
                    className="w-8 h-8 bg-primary hover:bg-primary/90 text-primary-foreground rounded flex items-center justify-center text-sm font-bold transition-all duration-200 hover:scale-105"
                  >
                    −
                  </button>
                </div>
              </div>
              <div className="absolute bottom-4 left-4 bg-background/90 backdrop-blur-sm rounded-lg p-3 border border-border/50 shadow-lg">
                <div className="text-xs text-muted-foreground space-y-1">
                  <p>🖱️ Drag to pan • 🔍 Scroll to zoom</p>
                  <p>🔵 Players • 🔴 Other entities</p>
                </div>
              </div>
              <BorderTrail
                className={`hover:scale-105 bg-linear-to-l from-orange-200 via-orange-500 to-orange-200 dark:from-orange-400 dark:via-orange-500 dark:to-orange-700`}
                size={340}
              />
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={340}
          />
        </Card>

        <Card className="bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle>Map Controls</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground">Zoom Level</label>
              <input
                type="range"
                min="0.01"
                max="2"
                step="0.01"
                value={scale}
                onChange={(e) => setScale(parseFloat(e.target.value))}
                className="w-full mt-2"
              />
              <div className="text-xs text-muted-foreground mt-1">{scale.toFixed(2)}x</div>
            </div>

            <div className="space-y-2">
              <button
                onClick={() => {
                  const player = entities.find(e => e.type === 'player');
                  if (player) {
                    setCenterX(player.x);
                    setCenterZ(player.z);
                  }
                }}
                className="w-full bg-secondary hover:bg-secondary/80 text-secondary-foreground px-3 py-2 rounded-md transition-colors text-sm"
              >
                Center on Player
              </button>
              <button
                onClick={() => {
                  setCenterX(0);
                  setCenterZ(0);
                  setScale(0.1);
                }}
                className="w-full bg-secondary hover:bg-secondary/80 text-secondary-foreground px-3 py-2 rounded-md transition-colors text-sm"
              >
                Reset View
              </button>
            </div>

            <div className="pt-4 border-t border-none">
              <h4 className="text-sm font-medium mb-2">Legend</h4>
              <div className="space-y-2 text-xs">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 bg-blue-500 rounded-full"></div>
                  <span>Players</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                  <span>Entities</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 border border-muted-foreground rounded"></div>
                  <span>Grid</span>
                </div>
              </div>
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={140}
          />
        </Card>
      </div>
    </div>
  );
}
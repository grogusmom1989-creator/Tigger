package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiRose
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PastelSparkle3DWorld(
    modifier: Modifier = Modifier
) {
    val htmlContent = remember {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    overflow: hidden;
                    background: radial-gradient(circle at center, #231C35 0%, #110E1B 100%);
                    touch-action: none;
                    user-select: none;
                    -webkit-user-select: none;
                }
                #canvas-container {
                    width: 100vw;
                    height: 100vh;
                }
            </style>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
        </head>
        <body>
            <div id="canvas-container"></div>
            <script>
                // Three.js Gentle Pastel Sparkle World
                const container = document.getElementById('canvas-container');
                const scene = new THREE.Scene();
                scene.fog = new THREE.FogExp2(0x191428, 0.05);

                const camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 0.1, 100);
                camera.position.set(0, 0, 7.5);

                const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
                renderer.setSize(window.innerWidth, window.innerHeight);
                renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
                container.appendChild(renderer.domElement);

                // Gentle ambient and pastel lights
                const ambientLight = new THREE.AmbientLight(0xFFE4F0, 1.2);
                scene.add(ambientLight);

                const dirLight1 = new THREE.DirectionalLight(0xFFD1DC, 1.4); // Pastel Pink
                dirLight1.position.set(5, 8, 5);
                scene.add(dirLight1);

                const dirLight2 = new THREE.DirectionalLight(0xCCE7FF, 1.2); // Soft Baby Blue
                dirLight2.position.set(-5, -4, 4);
                scene.add(dirLight2);

                const goldPoint = new THREE.PointLight(0xFFEAA7, 2, 10); // Warm Gold Glow
                goldPoint.position.set(0, 1, 3);
                scene.add(goldPoint);

                // --- 1. Central 3D Crown ---
                const crownGroup = new THREE.Group();

                // Crown base ring
                const ringGeo = new THREE.CylinderGeometry(1.3, 1.35, 0.35, 32, 1, true);
                const goldMat = new THREE.MeshStandardMaterial({
                    color: 0xFEE440,
                    metalness: 0.7,
                    roughness: 0.25,
                    side: THREE.DoubleSide
                });
                const ring = new THREE.Mesh(ringGeo, goldMat);
                crownGroup.add(ring);

                // Crown peaks
                const peakCount = 5;
                for (let i = 0; i < peakCount; i++) {
                    const angle = (i / peakCount) * Math.PI * 2;
                    const peakGeo = new THREE.ConeGeometry(0.28, 0.75, 4);
                    const peakMat = new THREE.MeshStandardMaterial({
                        color: 0xFFD166,
                        metalness: 0.8,
                        roughness: 0.2
                    });
                    const peak = new THREE.Mesh(peakGeo, peakMat);
                    peak.position.set(Math.cos(angle) * 1.3, 0.45, Math.sin(angle) * 1.3);
                    peak.rotation.y = -angle;
                    crownGroup.add(peak);

                    // Gem pearl on top of each peak
                    const gemGeo = new THREE.SphereGeometry(0.12, 16, 16);
                    const gemMat = new THREE.MeshStandardMaterial({
                        color: (i % 2 === 0) ? 0xFF85A1 : 0x70D6FF,
                        roughness: 0.1,
                        metalness: 0.3
                    });
                    const gem = new THREE.Mesh(gemGeo, gemMat);
                    gem.position.set(Math.cos(angle) * 1.3, 0.85, Math.sin(angle) * 1.3);
                    crownGroup.add(gem);
                }
                scene.add(crownGroup);

                // --- 2. Floating Pastel Hearts & Stars ---
                const floatingItems = [];

                // Heart Shape Extrusion Function
                function createHeartMesh(colorHex) {
                    const x = 0, y = 0;
                    const heartShape = new THREE.Shape();
                    heartShape.moveTo(x + 0.25, y + 0.25);
                    heartShape.bezierCurveTo(x + 0.25, y + 0.25, x + 0.20, y, x, y);
                    heartShape.bezierCurveTo(x - 0.30, y, x - 0.30, y + 0.35, x - 0.30, y + 0.35);
                    heartShape.bezierCurveTo(x - 0.30, y + 0.55, x - 0.10, y + 0.77, x + 0.25, y + 0.95);
                    heartShape.bezierCurveTo(x + 0.60, y + 0.77, x + 0.80, y + 0.55, x + 0.80, y + 0.35);
                    heartShape.bezierCurveTo(x + 0.80, y + 0.35, x + 0.80, y, x + 0.50, y);
                    heartShape.bezierCurveTo(x + 0.35, y, x + 0.25, y + 0.25, x + 0.25, y + 0.25);

                    const extrudeSettings = { depth: 0.15, bevelEnabled: true, bevelSegments: 3, steps: 1, bevelSize: 0.05, bevelThickness: 0.05 };
                    const geo = new THREE.ExtrudeGeometry(heartShape, extrudeSettings);
                    geo.center();
                    const mat = new THREE.MeshStandardMaterial({ color: colorHex, roughness: 0.3, metalness: 0.2 });
                    return new THREE.Mesh(geo, mat);
                }

                // Star Mesh Function
                function createStarMesh(colorHex) {
                    const geo = new THREE.OctahedronGeometry(0.35, 0);
                    const mat = new THREE.MeshStandardMaterial({ color: colorHex, metalness: 0.6, roughness: 0.2 });
                    return new THREE.Mesh(geo, mat);
                }

                const pastelPalette = [0xFF99C8, 0xFCF6BD, 0xD0F4DE, 0xA9DEF9, 0xE4C1F9];

                for (let i = 0; i < 18; i++) {
                    const isHeart = i % 2 === 0;
                    const col = pastelPalette[i % pastelPalette.length];
                    const item = isHeart ? createHeartMesh(col) : createStarMesh(col);

                    const radius = 2.8 + Math.random() * 2.5;
                    const theta = Math.random() * Math.PI * 2;
                    const phi = (Math.random() - 0.5) * Math.PI;

                    item.position.x = radius * Math.cos(phi) * Math.sin(theta);
                    item.position.y = radius * Math.sin(phi);
                    item.position.z = radius * Math.cos(phi) * Math.cos(theta);

                    item.scale.setScalar(0.7 + Math.random() * 0.4);
                    item.userData = {
                        rotSpeedX: (Math.random() - 0.5) * 0.02,
                        rotSpeedY: (Math.random() - 0.5) * 0.02,
                        floatSpeed: 0.0015 + Math.random() * 0.002,
                        initialY: item.position.y,
                        phase: Math.random() * Math.PI * 2
                    };

                    scene.add(item);
                    floatingItems.push(item);
                }

                // --- 3. Stardust Particle Cloud ---
                const particleCount = 120;
                const particleGeo = new THREE.BufferGeometry();
                const posArray = new Float32Array(particleCount * 3);

                for (let i = 0; i < particleCount * 3; i++) {
                    posArray[i] = (Math.random() - 0.5) * 16;
                }
                particleGeo.setAttribute('position', new THREE.BufferAttribute(posArray, 3));

                const particleMat = new THREE.PointsMaterial({
                    size: 0.08,
                    color: 0xFFF3B0,
                    transparent: true,
                    opacity: 0.8
                });
                const particleCloud = new THREE.Points(particleGeo, particleMat);
                scene.add(particleCloud);

                // --- 4. Smooth Drag Rotation Controls ---
                let isDragging = false;
                let previousMousePosition = { x: 0, y: 0 };
                let targetRotationX = 0.2;
                let targetRotationY = 0;

                function onPointerDown(e) {
                    isDragging = true;
                    const clientX = e.clientX || (e.touches && e.touches[0].clientX);
                    const clientY = e.clientY || (e.touches && e.touches[0].clientY);
                    previousMousePosition = { x: clientX, y: clientY };
                }

                function onPointerMove(e) {
                    if (!isDragging) return;
                    const clientX = e.clientX || (e.touches && e.touches[0].clientX);
                    const clientY = e.clientY || (e.touches && e.touches[0].clientY);
                    const deltaX = clientX - previousMousePosition.x;
                    const deltaY = clientY - previousMousePosition.y;

                    targetRotationY += deltaX * 0.008;
                    targetRotationX += deltaY * 0.008;
                    previousMousePosition = { x: clientX, y: clientY };
                }

                function onPointerUp() {
                    isDragging = false;
                }

                window.addEventListener('mousedown', onPointerDown);
                window.addEventListener('mousemove', onPointerMove);
                window.addEventListener('mouseup', onPointerUp);

                window.addEventListener('touchstart', onPointerDown, { passive: true });
                window.addEventListener('touchmove', onPointerMove, { passive: true });
                window.addEventListener('touchend', onPointerUp);

                // --- 5. Gentle Animation Loop ---
                let clock = new THREE.Clock();

                function animate() {
                    requestAnimationFrame(animate);
                    const elapsedTime = clock.getElapsedTime();

                    // Calm, slow automatic crown spin
                    if (!isDragging) {
                        targetRotationY += 0.003;
                    }

                    crownGroup.rotation.y += (targetRotationY - crownGroup.rotation.y) * 0.08;
                    crownGroup.rotation.x += (targetRotationX - crownGroup.rotation.x) * 0.08;
                    crownGroup.position.y = Math.sin(elapsedTime * 1.2) * 0.15; // Gentle float

                    // Floating hearts and stars
                    floatingItems.forEach(item => {
                        item.rotation.x += item.userData.rotSpeedX;
                        item.rotation.y += item.userData.rotSpeedY;
                        item.position.y = item.userData.initialY + Math.sin(elapsedTime * 1.5 + item.userData.phase) * 0.25;
                    });

                    // Particles gentle swirl
                    particleCloud.rotation.y = elapsedTime * 0.02;

                    renderer.render(scene, camera);
                }

                animate();

                window.addEventListener('resize', () => {
                    camera.aspect = window.innerWidth / window.innerHeight;
                    camera.updateProjectionMatrix();
                    renderer.setSize(window.innerWidth, window.innerHeight);
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KikiLilac.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .testTag("pastel_sparkle_3d_world"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KikiLilac.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pastel Sparkle 3D World",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Gentle floating stars, hearts & spinning crown",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KikiSurfaceElevated
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = KikiGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Drag to Rotate",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Three.js Canvas Container via AndroidView WebView
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(16.dp))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL("https://cdnjs.cloudflare.com", htmlContent, "text/html", "UTF-8", null)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Calm, gentle motion • Touch and drag anywhere on the crown to inspect the 3D sparkle sanctuary",
                style = MaterialTheme.typography.labelSmall,
                color = KikiTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

uniform sampler2D tex0;
varying vec4 depth;

void main (void)
{
	gl_FragColor = mix(vec4(0.8, 0.8, 0.8, 1.0), gl_Color * texture2D(tex0, gl_TexCoord[0].st), clamp(exp(-pow(0.01*length(depth), 2.0)), 0.0, 1.0));
}

varying vec4 diffuse,ambient,gambient;
varying vec3 normal,halfVector;
varying vec4 position,ecPos;

void main()
{
	position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_Position = position;
	ecPos = gl_ModelViewMatrix * gl_Vertex;
	gl_TexCoord[0]  = gl_MultiTexCoord0;
	normal = normalize(gl_NormalMatrix * gl_Normal);
	
	halfVector = gl_LightSource[0].halfVector.xyz;
	diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
	if(gl_LightSource[0].position.w == 0) {
		ambient = gl_FrontMaterial.ambient * (gl_LightSource[0].ambient + gl_LightModel.ambient);
	} else {
		ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
		gambient = gl_FrontMaterial.ambient * gl_LightModel.ambient;
	}
}
